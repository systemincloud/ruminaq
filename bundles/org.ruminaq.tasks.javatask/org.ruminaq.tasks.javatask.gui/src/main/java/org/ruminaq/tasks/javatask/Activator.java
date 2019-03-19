package org.ruminaq.tasks.javatask;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.ruminaq.tasks.AbstractTaskActivator;
import org.ruminaq.tasks.TasksManagerHandlerImpl;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.javatask.extension.JavaTaskExtensionManager;

public class Activator extends AbstractTaskActivator {

    private static Activator plugin;

    public  static Activator getDefault() {
        return plugin;
    }

	@Override
	public void start(BundleContext context) throws Exception {
	    plugin = this;
	    JavaTaskExtensionManager.INSTANCE.init(context);
        TasksManagerHandlerImpl.INSTANCE.init(context);
        super.start(context);
	}

   @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

	@Override
	protected ITaskApi getTaskApi(Bundle bundle) {
	    return new TaskApi(bundle.getSymbolicName(), bundle.getVersion());
	}

	@Override
	protected ITaskUiApi getTaskUiApi(Bundle bundle) {
	    return new TaskUiApi(bundle.getSymbolicName(), bundle.getVersion());
	}
}
