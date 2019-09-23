package org.ruminaq.launch.ui;

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Version;

public class Activator extends AbstractUIPlugin {

  private static Activator plugin;

  public Activator() {
    plugin = this;
  }

  public static Activator getDefault() {
    return plugin;
  }

  public static IWorkspace getWorkspace() {
    return ResourcesPlugin.getWorkspace();
  }

  public static URL getInstallURL() {
    return getDefault().getBundle().getEntry("/");
  }

  public static String getID() {
    return getDefault().getBundle().getSymbolicName();
  }

  public static Version getVersion() {
    return getDefault().getBundle().getVersion();
  }

  public static Shell getShell() {
    return getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
  }

  public static IWorkbenchPage getActivePage() {
    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
        .getActiveWorkbenchWindow();
    if (workbenchWindow != null)
      return workbenchWindow.getActivePage();
    return null;
  }
}
