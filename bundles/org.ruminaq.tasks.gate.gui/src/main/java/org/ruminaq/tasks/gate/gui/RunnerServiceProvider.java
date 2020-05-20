package org.ruminaq.tasks.gate.gui;

import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.gate.model.gate.GatePackage;

public final class RunnerServiceProvider extends AbstractRunnerService {
  @Override
  public void initModelPackages() {
    GatePackage.eINSTANCE.getClass();
  }
}
