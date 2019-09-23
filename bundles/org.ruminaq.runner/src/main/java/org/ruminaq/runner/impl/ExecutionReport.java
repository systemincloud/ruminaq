package org.ruminaq.runner.impl;

import org.ruminaq.runner.impl.GeneratorI.GeneratorCallable;

public class ExecutionReport {

  private boolean moreTimes;

  public boolean isMoreTimes() {
    return moreTimes;
  }

  private TaskI task;

  public TaskI getTask() {
    return task;
  }

  public ExecutionReport(boolean moreTimes, TaskI task) {
    this.moreTimes = moreTimes;
    this.task = task;
  }

  public ExecutionReport(GeneratorCallable generatorCallable) {
    this.moreTimes = false;
    this.task = generatorCallable.getGeneratorI();
  }

}
