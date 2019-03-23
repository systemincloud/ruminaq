package org.ruminaq.tasks.rtask;

import org.apache.commons.cli.Options;
import org.ruminaq.model.sic.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.rtask.impl.RTaskI;

public final class RunnerServiceProvider extends AbstractRunnerService {
	@Override
	public void initModelPackages() {
		RtaskPackage.eINSTANCE.getClass();
	}

	@Override
	public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
		return task instanceof RTask ? new RTaskI(parent, task) : null;
	}

	@Override
	public void addOptions(Options options) {
		options.addOption(RTaskI.ATTR_R_HOME, true, "");
		options.addOption(RTaskI.ATTR_R_POLICY, true, "");
		options.addOption(RTaskI.ATTR_R_LIBS_USR, true, "");
		options.addOption(RTaskI.ATTR_R_RJPATH, true, "");
	}
}
