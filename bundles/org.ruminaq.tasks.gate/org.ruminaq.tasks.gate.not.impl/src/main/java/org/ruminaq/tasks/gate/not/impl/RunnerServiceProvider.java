package org.ruminaq.tasks.gate.not.impl;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.gate.not.impl.NotI;
import org.ruminaq.tasks.gate.not.model.not.Not;
import org.ruminaq.tasks.gate.not.model.not.NotPackage;

public final class RunnerServiceProvider extends AbstractRunnerService {
	@Override
	public void initModelPackages() {
		NotPackage.eINSTANCE.getClass();
	}

	@Override
	public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
		return task instanceof Not ? new NotI(parent, task) : null;
	}
}
