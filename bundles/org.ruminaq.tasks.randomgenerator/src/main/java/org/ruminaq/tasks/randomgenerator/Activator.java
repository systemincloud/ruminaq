package org.ruminaq.tasks.randomgenerator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.ruminaq.tasks.AbstractTaskActivator;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.randomgenerator.extension.RandomGeneratorExtensionManager;

public class Activator extends AbstractTaskActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        RandomGeneratorExtensionManager.INSTANCE.init(context);
        super.start(context);
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
