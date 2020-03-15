package org.ruminaq.runner.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.desc.PortsDescrUtil;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.DataI;

import ch.qos.logback.classic.Logger;
import com.google.common.base.Joiner;

public abstract class BasicTaskI extends TaskI {

  private final Logger logger = RunnerLoggerFactory.getLogger(BasicTaskI.class);

  private List<TaskExecutor> executors = Collections
      .synchronizedList(new LinkedList<TaskExecutor>());
  private Lock executorsLock = new ReentrantLock();

  protected void addExternalSrcExecNb(int externalSrcExecNb) {
    executorsLock.lock();
    while (externalSrcExecNb-- > 0)
      executors.add(new ExtTaskExecutor());
    executorsLock.unlock();
  }

  @Override
  public List<BasicTaskI> getReadyTasks() {
    return Arrays.asList(BasicTaskI.this);
  }

  public BasicTaskI(EmbeddedTaskI parent, Task task) {
    super(parent, task);
  }

  protected void executeAsync(String portId, DataI data) {
  }

  protected void execute(PortMap portIdData, int grp) {
  }

  protected void executeExternalSrc() {
  }

  protected void executeConstant() {
  }

  protected interface TaskExecutor {
    void execute();
  }

  private class AsyncTaskExecutor implements TaskExecutor {
    private final InternalInputPortI iip;

    public AsyncTaskExecutor(InternalInputPortI iip) {
      this.iip = iip;
      iip.reserveData();
    }

    @Override
    public void execute() {
      logger.trace("{}:{}: async", this.getClass().getSimpleName(),
          BasicTaskI.this.getId());
      executeAsync(iip.getPortId(), iip.getData());
      logger.trace("{}:{}: async finished", this.getClass().getSimpleName(),
          BasicTaskI.this.getId());
    }
  }

  private class SyncTaskExecutor implements TaskExecutor {
    private final int group;

    public SyncTaskExecutor(int group) {
      this.group = group;
      for (InternalInputPortI iip : internalInputPorts.values()) {
        if (!iip.isAsynchronous() && iip.getGroup() == group)
          iip.reserveData();
      }
    }

    @Override
    public void execute() {
      logger.trace("{}:{}: sync", this.getClass().getSimpleName(),
          BasicTaskI.this.getId());
      PortMap portIdData = new PortMap();
      for (InternalInputPortI iip : internalInputPorts.values()) {
        if (!iip.isAsynchronous() && iip.getGroup() == group)
          portIdData.put(iip.getPortId(), iip.getData());
      }
      BasicTaskI.this.execute(portIdData, group);
      logger.trace("{}:{}: sync finished", this.getClass().getSimpleName(),
          BasicTaskI.this.getId());
    }
  }

  private class ExtTaskExecutor implements TaskExecutor {
    @Override
    public void execute() {
      logger.trace("{}:{}: external src", this.getClass().getSimpleName(),
          BasicTaskI.this.getId());
      executeExternalSrc();
      logger.trace("{}:{}: external src finished",
          this.getClass().getSimpleName(), BasicTaskI.this.getId());
    }
  }

  @Override
  public ExecutionReport call() {
    try {
      logger.debug("{}:{}: execute", this.getClass().getSimpleName(),
          this.getId());
      if (Thread.interrupted())
        return new ExecutionReport(false, this);

      if (executors.size() > 0) {
        TaskExecutor ex = null;
        executorsLock.lock();
        for (TaskExecutor e : executors)
          if (e instanceof AsyncTaskExecutor) {
            ex = e;
            break;
          }
        if (ex == null)
          ex = executors.remove(0);
        else
          executors.remove(ex);
        executorsLock.unlock();
        logger.trace("Executor chosen {}", ex.getClass().getSimpleName());

        executionWrapper(ex);
      }

      boolean onceAgain = executors.size() > 0;

      logger.trace("Once again {}", onceAgain);

      return new ExecutionReport(onceAgain, this);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("\n{}\n{}", e.getMessage(),
          Joiner.on("\n").join(e.getStackTrace()));
    }
    return new ExecutionReport(false, this);
  }

  private Lock execOne = new ReentrantLock();

  protected void executionWrapper(TaskExecutor ex) throws InterruptedException {
    execOne.lock();
    ex.execute();
    waitForSync();
    execOne.unlock();
  }

  protected void waitForSync() {
    lockOutputs.lock();
    while (isWaitingForSync()) {
      logger.trace("{}:{}: waiting for synchronization to be done",
          this.getClass().getSimpleName(), this.getId());
      try {
        outputsSync.await();
      } catch (InterruptedException e) {
        logger.trace("{}:{}: InterruptedException",
            this.getClass().getSimpleName(), this.getId());
      }
    }
    resetSync();
    lockOutputs.unlock();
  }

  public void putData(PortsDescr pd, DataI data) {
    putData(PortsDescrUtil.getName(pd), data);
  }

  public void putData(String id, DataI data) {
    logger.trace("{}:{}: put data on {}", this.getClass().getSimpleName(),
        this.getId(), id);
    if (isConstant())
      data.setConstant(true);
    internalOutputPorts.get(id).putData(data);
  }

  public void putData(PortsDescr pd, int idx, DataI data) {
    putData(PortsDescrUtil.getName(pd) + " " + idx, data);
  }

  @Override
  public void portManager(InternalInputPortI input) {
    if (!isAtomic() || input.isAsynchronous()) {
      executorsLock.lock();
      executors.add(new AsyncTaskExecutor(input));
      executorsLock.unlock();
      setReadyWithParents(true);
    } else {
      logger.trace("{}:{}:{}: is synchronous", this.getClass().getSimpleName(),
          this.getId(), input.getPortId());
      if (checkSync(input.getGroup())) {
        executorsLock.lock();
        executors.add(new SyncTaskExecutor(input.getGroup()));
        executorsLock.unlock();
        logger.trace("{}:{}: set ready with parents",
            this.getClass().getSimpleName(), this.getId());
        setReadyWithParents(true);
      }
    }
  }

  private boolean checkSync(int grp) {
    logger.trace("checkSync");
    boolean onlyHold = true;
    boolean sync = true;
    boolean hasHoldNotRead = false;

    for (InternalInputPortI iip : internalInputPorts.values()) {
      if (!iip.isAsynchronous() && iip.getGroup() == grp) {
        boolean hasData = iip.hasData();
        boolean holdLast = iip.isHoldLast();
        boolean lastRead = iip.isHoldLastRead();

        sync = sync && hasData;

        if (!holdLast)
          onlyHold = false;
        if (holdLast && !lastRead)
          hasHoldNotRead = true;

        logger.trace("{} hasData: {}, holdLast: {}, lastRead {}",
            iip.getPortId(), hasData, holdLast, lastRead);
      }
    }

    //
    // sync : all synchronous ports has data in the queue
    // hasHold : there is at least one synchronous port that can hold last data
    // hasHoldNotRead : there is at least one synchronous port that can hold
    // last data and this data hasn't been read
    //
    logger.trace("sync {}, onlyHold {}, hasHoldNotRead {}", sync, onlyHold,
        hasHoldNotRead);
    return onlyHold ? sync && hasHoldNotRead : sync;
  }
}
