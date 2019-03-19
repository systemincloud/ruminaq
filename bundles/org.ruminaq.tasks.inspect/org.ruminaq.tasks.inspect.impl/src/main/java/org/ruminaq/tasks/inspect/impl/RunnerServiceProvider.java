package org.ruminaq.tasks.inspect.impl;

import org.ruminaq.model.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.inspect.impl.InspectI;
import org.ruminaq.tasks.inspect.model.inspect.Inspect;
import org.ruminaq.tasks.inspect.model.inspect.InspectPackage;

public final class RunnerServiceProvider extends AbstractRunnerService {
	@Override
	public void initModelPackages() {
		InspectPackage.eINSTANCE.getClass();
	}

	@Override
	public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
		return task instanceof Inspect ? new InspectI(parent, task) : null;
	}
}
