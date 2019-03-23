package org.ruminaq.tasks.sipo.ui.properties;

import org.osgi.framework.FrameworkUtil;
import org.ruminaq.launch.LaunchListener;
import org.ruminaq.launch.RuminaqLaunchDelegate;
import org.ruminaq.tasks.AbstractTaskPropertySection;

public class PropertySection extends AbstractTaskPropertySection implements LaunchListener {

	@Override
	protected String getPrefix() {
		String symbolicName = FrameworkUtil.getBundle(getClass()).getSymbolicName();
		return symbolicName.substring(0, symbolicName.length() - ".ui".length());
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
