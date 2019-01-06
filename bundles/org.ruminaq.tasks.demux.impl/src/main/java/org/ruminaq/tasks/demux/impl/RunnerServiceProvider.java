package org.ruminaq.tasks.demux.impl;

import org.ruminaq.model.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.demux.model.demux.Demux;
import org.ruminaq.tasks.demux.model.demux.DemuxPackage;

public final class RunnerServiceProvider extends AbstractRunnerService {
	@Override
	public void initModelPackages() {
		DemuxPackage.eINSTANCE.getClass();
	}

	@Override
	public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
		return task instanceof Demux ? new DemuxI(parent, task) : null;
	}
}
