/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
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
      String basePath) {
    logger.trace("Creating Task {}", task.getId());
    TaskI taskI = RunnerServiceManager.INSTANCE.getImplementation(parent, task);
    if (taskI != null)
      return taskI;
    else if (task instanceof EmbeddedTask)
      return new EmbeddedTaskI(parent, task,
          basePath + ((EmbeddedTask) task).getImplementationTask(),
          ((EmbeddedTask) task).getParameters());
    else {

    }

    return null;
  }

}
