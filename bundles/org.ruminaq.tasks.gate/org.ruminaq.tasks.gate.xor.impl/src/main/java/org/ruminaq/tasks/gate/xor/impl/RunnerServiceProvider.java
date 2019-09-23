package org.ruminaq.tasks.gate.xor.impl;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.gate.xor.impl.XorI;
import org.ruminaq.tasks.gate.xor.model.xor.Xor;
import org.ruminaq.tasks.gate.xor.model.xor.XorPackage;

public final class RunnerServiceProvider extends AbstractRunnerService {
  @Override
  public void initModelPackages() {
    XorPackage.eINSTANCE.getClass();
  }

  @Override
  public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
    return task instanceof Xor ? new XorI(parent, task) : null;
  }
}
