package org.ruminaq.gui.features;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.ruminaq.consts.Constants.SicPlugin;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = SicPlugin.GUI_ID.s();

	private static Activator plugin;

	public Activator() { }

	@Override
	public void start(BundleContext context) throws Exception {
		Images.init(this.getBundle());
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

}
