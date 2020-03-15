package org.ruminaq.runner.impl;

import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.service.RunnerServiceManager;
import org.slf4j.Logger;

public enum TaskImplementationFactory {

  INSTANCE;

  private final Logger logger = RunnerLoggerFactory
      .getLogger(TaskImplementationFactory.class);

  public TaskI getImplementation(EmbeddedTaskI parent, Task task,
      String basePath, boolean inCloud, boolean runOnlyLocalTasks) {
    logger.trace("Creating Task {}", task.getId());
    TaskI taskI = RunnerServiceManager.INSTANCE.getImplementation(parent, task);
    if (taskI != null)
      return taskI;
    else if (task instanceof EmbeddedTask)
      return new EmbeddedTaskI(parent, task,
          basePath + ((EmbeddedTask) task).getImplementationTask(),
          ((EmbeddedTask) task).getParameters(), inCloud, runOnlyLocalTasks);
    else {

    }

    return null;
  }

}
