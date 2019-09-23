package org.ruminaq.tasks.sipo.impl;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.sipo.model.sipo.Sipo;
import org.ruminaq.tasks.sipo.model.sipo.SipoPackage;

public final class RunnerServiceProvider extends AbstractRunnerService {

  @Override
  public void initModelPackages() {
    SipoPackage.eINSTANCE.getClass();
  }

  @Override
  public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
    return task instanceof Sipo ? new SipoI(parent, task) : null;
  }
}
