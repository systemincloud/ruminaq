package org.ruminaq.prefs;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	private static Activator plugin;
	public  static Activator getDefault() { return plugin; }

	public Activator() { }

	@Override public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
}
