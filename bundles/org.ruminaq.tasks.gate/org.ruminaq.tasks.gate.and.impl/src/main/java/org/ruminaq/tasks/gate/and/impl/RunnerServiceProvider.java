package org.ruminaq.tasks.gate.and.impl;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.gate.and.model.and.AndPackage;
import org.ruminaq.tasks.gate.and.model.and.And;

public final class RunnerServiceProvider extends AbstractRunnerService {
  @Override
  public void initModelPackages() {
    AndPackage.eINSTANCE.getClass();
  }

  @Override
  public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
    return task instanceof And ? new AndI(parent, task) : null;
  }
}
