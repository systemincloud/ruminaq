package org.ruminaq.tasks.randomgenerator;

import org.ruminaq.model.sic.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;

public final class RunnerServiceProvider extends AbstractRunnerService {

	@Override
	public void initModelPackages() {
		RandomgeneratorPackage.eINSTANCE.getClass();
	}

	@Override
	public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
		return task instanceof RandomGenerator ? new RandomGeneratorI(parent, task) : null;
	}
}
