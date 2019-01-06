package org.ruminaq.tasks.javatask;

import org.ruminaq.model.sic.Task;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.tasks.javatask.impl.JavaTaskI;

public final class RunnerServiceProvider extends AbstractRunnerService {
	@Override
	public void initModelPackages() {
		JavataskPackage.eINSTANCE.getClass();
	}

	@Override
	public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
		return task instanceof JavaTask ? new JavaTaskI(parent, task) : null;
	}
}
