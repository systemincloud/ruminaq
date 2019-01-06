package org.ruminaq.tasks.javatask.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.ruminaq.tasks.AbstractTaskUiActivator;

import org.ruminaq.debug.extension.ModelerDebugExtension;

public class Activator extends AbstractTaskUiActivator {

	private static Activator plugin;
	public  static Activator getDefault() { return plugin; }
	
	ServiceRegistration<?> modelerExtensionServiceRegistration;
	
    @Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
    	ModelerDebugExtension debugExtensionService = new JavaTasksDebugModelerExtension();
    	modelerExtensionServiceRegistration = context.registerService(ModelerDebugExtension.class.getName(), 
    			                                                      debugExtensionService, 
        															  null);
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
    	modelerExtensionServiceRegistration.unregister();
		super.stop(context);
	}

	@Override
	public String getBundlePrefix() {
		String symbolicName = plugin.getBundle().getSymbolicName();
		return symbolicName.substring(0, symbolicName.length() - ".ui".length()); //$NON-NLS-1$
	}
	
	public static Image getImage(String file) {
		Bundle bundle = FrameworkUtil.getBundle(Activator.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}
}
