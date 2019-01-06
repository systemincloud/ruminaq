package org.ruminaq.tasks.randomgenerator.ui.properties;

import java.util.List;
import org.ruminaq.launch.LaunchListener;
import org.ruminaq.launch.RuminaqLaunchDelegate;
import org.ruminaq.tasks.AbstractTaskPropertySection;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.randomgenerator.ui.Activator;

public class PropertySection extends AbstractTaskPropertySection implements LaunchListener {
	@Override
	protected List<ITaskUiApi> getTasks() {
		return Activator.getDefault().getTasksUiManager().getTasks();
	}

	@Override
	protected void initLaunchListener() {
		RuminaqLaunchDelegate.addLaunchListener(this);
	}

	@Override
	protected boolean isRunning() {
		return RuminaqLaunchDelegate.isRunning();
	}

	@Override
	public void launched() {
		super.aLaunched();
	}

	@Override
	public void stopped() {
		super.aStopped();
	}

	@Override
	public void dirmiStarted() {
		super.aDirmiStarted();
	}
}
