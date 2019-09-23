package org.ruminaq.tasks.gate.or.impl;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.gate.or.impl.OrI;
import org.ruminaq.tasks.gate.or.model.or.Or;
import org.ruminaq.tasks.gate.or.model.or.OrPackage;

public final class RunnerServiceProvider extends AbstractRunnerService {
  @Override
  public void initModelPackages() {
    OrPackage.eINSTANCE.getClass();
  }

  @Override
  public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
    return task instanceof Or ? new OrI(parent, task) : null;
  }
}
