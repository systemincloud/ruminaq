package org.ruminaq.runner.impl;

import java.util.concurrent.Callable;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import ch.qos.logback.classic.Logger;

public abstract class GeneratorI extends BasicTaskI {

  private final Logger logger = RunnerLoggerFactory.getLogger(GeneratorI.class);

  private GeneratorCallable generatorCallable = new GeneratorCallable();

  public GeneratorI(EmbeddedTaskI parent, Task task) {
    super(parent, task);
  }

  private boolean run = true;

  private long start = Long.MAX_VALUE;
  private long sleep = 1000;

  protected void sleep(long l) {
    start = System.currentTimeMillis();
    this.sleep = l;
  }

  protected abstract void generate();

  private boolean paused = false;

  public synchronized void pause() {
    this.paused = true;
  }

  public boolean isPaused() {
    return this.paused;
  }

  public synchronized void resume() {
    this.paused = false;
    logger.trace("{}:{}: Resume loop", this.getClass().getSimpleName(),
        GeneratorI.this.getId());
    synchronized (generatorCallable) {
      generatorCallable.notify();
    }
  }

  public void end() {
    this.run = false;
  }

  private class GeneratorExecutor implements TaskExecutor {
    @Override
    public void execute() {
      generate();
    }
  }

  public class GeneratorCallable implements Callable<ExecutionReport> {
    @Override
    public ExecutionReport call() throws Exception {
      logger.trace("{}:{}: Start loop", this.getClass().getSimpleName(),
          GeneratorI.this.getId());
      while (run) {
        logger.trace("{}:{}: generate", this.getClass().getSimpleName(),
            GeneratorI.this.getId());

        start = System.currentTimeMillis();
        executionWrapper(new GeneratorExecutor());

        long end = System.currentTimeMillis();
        long duration = end - start;

        long interval = sleep - duration;
        logger.trace("{}:{}: interval is {}", this.getClass().getSimpleName(),
            GeneratorI.this.getId(), interval);

        if (interval > 0)
          try {
            Thread.sleep(interval);
          } catch (InterruptedException e) {
            break;
          }

        if (paused) {
          logger.trace("{}:{}: Pause loop", this.getClass().getSimpleName(),
              GeneratorI.this.getId());
          synchronized (generatorCallable) {
            try {
              generatorCallable.wait();
            } catch (InterruptedException e) {
              break;
            }
          }
          logger.trace("{}:{}: Resumed loop", this.getClass().getSimpleName(),
              GeneratorI.this.getId());
        }
      }
      logger.trace("{}:{}: Stop loop", this.getClass().getSimpleName(),
          GeneratorI.this.getId());

      return new ExecutionReport(this);
    }

    public GeneratorI getGeneratorI() {
      return GeneratorI.this;
    }
  }

  public GeneratorCallable getGenerateCallable() {
    return generatorCallable;
  }
}
