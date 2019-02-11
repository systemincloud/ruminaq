package org.modeler.tasks.console.ui.views;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.FrameworkUtil;
import org.ruminaq.tasks.AbstractTaskViewPart;
import org.ruminaq.util.EclipseUtil;

public class ConsoleViewPart extends AbstractTaskViewPart {

	@Override
	protected String getPrefix() {
		String symbolicName = FrameworkUtil.getBundle(getClass()).getSymbolicName();
		return symbolicName.substring(0, symbolicName.length() - ".ui".length());
	}

	static {
		PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {
			@Override
			public boolean preShutdown(IWorkbench window, boolean arg1) {
				EclipseUtil.closeAllViews(ConsoleViewPart.class);
				return true;
			}
			@Override
			public void postShutdown(IWorkbench window) { }
		});
	}
}
