package org.ruminaq.tasks.demux.impl;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.mux.model.mux.Demux;
import org.ruminaq.tasks.mux.model.mux.MuxPackage;

public final class RunnerServiceProvider extends AbstractRunnerService {
  @Override
  public void initModelPackages() {
    MuxPackage.eINSTANCE.getClass();
  }

  @Override
  public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
    return task instanceof Demux ? new DemuxI(parent, task) : null;
  }
}
