package org.ruminaq.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.ruminaq.runner.impl.ExecutionReport;
import org.ruminaq.runner.impl.GeneratorI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.impl.debug.DebugI;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.EndTaskEvent;
import org.ruminaq.runner.impl.debug.events.debugger.ResumedEvent;
import org.ruminaq.runner.impl.debug.events.debugger.SubmitTaskEvent;
import org.ruminaq.runner.impl.debug.events.debugger.SuspendedEvent;
import ch.qos.logback.classic.Logger;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class Engine {

  private final Logger logger = RunnerLoggerFactory.getLogger(Engine.class);

  private AdapterTask adapterTask;
  private ListeningExecutorService generatorsExecutor = MoreExecutors
      .listeningDecorator(Executors.newCachedThreadPool());
  private List<ListenableFuture<ExecutionReport>> generatorFutures = new ArrayList<>();
  private ListeningExecutorService tasksExecutor = MoreExecutors
      .listeningDecorator(Executors.newCachedThreadPool());
  private List<ListenableFuture<ExecutionReport>> tasksFutures = new ArrayList<>();

  private List<TaskI> runningReadyTasks = Collections
      .synchronizedList(new LinkedList<TaskI>());

  private Lock lockEngine = new ReentrantLock();

  public Lock getLock() {
    return lockEngine;
  }

  private Condition condEngine = lockEngine.newCondition();

  public Condition getCondition() {
    return condEngine;
  }

  private volatile boolean toBeBroke = false;

  public Engine(AdapterTask adapterTask) {
    this.adapterTask = adapterTask;
    adapterTask.setEngine(this);
  }

  public void run() {
    try {
      adapterTask.runnerStart();
      logger.trace("Start engine");

      adapterTask.executeConstants();

      for (final GeneratorI gen : adapterTask.getAllGenerators()) {
        logger.debug("{}:{}: start", gen.getClass().getSimpleName(),
            gen.getId());
        ListenableFuture<ExecutionReport> g = generatorsExecutor
            .submit(gen.getGenerateCallable());
        generatorFutures.add(g);
        Futures.addCallback(g, new FutureCallback<ExecutionReport>() {
          @Override
          public void onSuccess(ExecutionReport r) {
            logger.trace("{} finished", r.getTask().getId());
            lockEngine.lock();
            condEngine.signal();
            lockEngine.unlock();
          }

          @Override
          public void onFailure(Throwable thrown) {
            thrown.printStackTrace();
            logger.error("{}:{}: failure", gen.getClass().getSimpleName(),
                gen.getId());
            logger.error("\n{}\n{}", thrown.getMessage(),
                Joiner.on("\n").join(thrown.getStackTrace()));
          }
        }, MoreExecutors.directExecutor());
      }

      while (true) {
        lockEngine.lock();
        logger.trace("Start engine loop");
        generatorFutures.removeAll(generatorFutures.stream()
            .filter(Future::isDone).collect(Collectors.toList()));
        tasksFutures.removeAll(tasksFutures.stream().filter(Future::isDone)
            .collect(Collectors.toList()));

        boolean generatorFuturesEmpty = generatorFutures.isEmpty();
        boolean tasksFuturesEmpty = tasksFutures.isEmpty();
        boolean mainTaskIReady = adapterTask.isReady();
        boolean mainTaskExternalSource = adapterTask.hasExternalSource();

        logger.trace("generatorFutures empty: {}", generatorFuturesEmpty);
        logger.trace("tasksFutures empty: {}", tasksFuturesEmpty);
        logger.trace("mainTask ready: {}", mainTaskIReady);
        logger.trace("mainTask hasExternalSrc: {}", mainTaskExternalSource);

        if (generatorFuturesEmpty && tasksFuturesEmpty && !mainTaskIReady
            && !mainTaskExternalSource) {
          lockEngine.unlock();
          break;
        }
        if (toBeBroke) {
          lockEngine.unlock();
          break;
        }

        if (mainTaskIReady) {
          LinkedList<TaskI> rts = adapterTask.getReadyTasks();

          // No ready task that is not running
          rts.stream().filter(TaskI::isRunning)
              .filter(Predicate.not(runningReadyTasks::contains))
              .peek(t -> logger.trace("{} is running", t.getId()))
              .forEach(runningReadyTasks::add);
          if (!rts.isEmpty() && rts.size() == runningReadyTasks.size()) {
            logger.trace("Wait : all ready tasks are running");
            DebugI.INSTANCE.debug(new SuspendedEvent());
            try {
              condEngine.await();
            } catch (InterruptedException e) {
            }
            DebugI.INSTANCE.debug(new ResumedEvent());
          }
          // ---------------------------------------

          logger.trace("Found {} ready tasks", rts.size());

          Collections.sort(rts, new PriorityComparator());

          for (TaskI t : rts) {
            if (t.isRunning()) {
              logger.trace("{} is ready but already running", t.getId());
              continue; // Don't execute the same task more then once at the
                        // same time
            }
            t.setRunning(true);
            t.setReady(false);
            logger.trace("Task to submit {}", t.getId());
            DebugI.INSTANCE.debug(new SubmitTaskEvent());
            ListenableFuture<ExecutionReport> f = tasksExecutor.submit(t);
            tasksFutures.add(f);
            Futures.addCallback(f, new FutureCallback<ExecutionReport>() {
              @Override
              public void onSuccess(ExecutionReport r) {
                logger.trace("{} finished", r.getTask().getId());
                lockEngine.lock();
                r.getTask().setRunning(false);
                if (runningReadyTasks.contains(r.getTask())) {
                  runningReadyTasks.remove(r.getTask());
                }
                lockEngine.unlock();

                if (r.isMoreTimes()) {
                  logger.trace("{} once again ready", r.getTask().getId());
                  r.getTask().setReadyWithParents(true);
                } else {
                  lockEngine.lock();
                  condEngine.signal();
                  lockEngine.unlock();
                }
                DebugI.INSTANCE.debug(new EndTaskEvent());
                logger.trace("{} definitely finished", r.getTask().getId());
              }

              @Override
              public void onFailure(Throwable thrown) {
              }
            }, MoreExecutors.directExecutor());
          }
        } else {
          logger.trace("Wait : no ready task");
          DebugI.INSTANCE.debug(new SuspendedEvent());
          try {
            condEngine.await();
          } catch (InterruptedException e) {
          }
          DebugI.INSTANCE.debug(new ResumedEvent());
        }
        lockEngine.unlock();
      }

      generatorsExecutor.shutdown();
      tasksExecutor.shutdown();
      try {
        generatorsExecutor.awaitTermination(1500, TimeUnit.MILLISECONDS);
        tasksExecutor.awaitTermination(1500, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (!generatorsExecutor.isTerminated())
        generatorsExecutor.shutdownNow();
      if (!tasksExecutor.isTerminated())
        tasksExecutor.shutdownNow();

    } finally {
      adapterTask.runnerStop();
    }
  }

  public void breakRunner() {
    logger.trace("Break");
    this.toBeBroke = true;
  }

  public void modelEvent(IDebugEvent event) {
    adapterTask.modelEvent(event);
  }

  public void initDebugers() {
    adapterTask.initDebugers();
  }
}
