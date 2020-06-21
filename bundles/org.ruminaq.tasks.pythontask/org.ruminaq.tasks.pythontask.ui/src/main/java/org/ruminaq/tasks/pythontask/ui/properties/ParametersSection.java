package org.ruminaq.tasks.pythontask.ui.properties;

import org.osgi.framework.FrameworkUtil;
import org.ruminaq.launch.RuminaqLaunchDelegate;

public class ParametersSection {

  protected String getPrefix() {
    String symbolicName = FrameworkUtil.getBundle(getClass()).getSymbolicName();
    return symbolicName.substring(0, symbolicName.length() - ".ui".length());
  }

  protected void initLaunchListener() {
//    RuminaqLaunchDelegate.addLaunchListener(this);
  }

  protected boolean isRunning() {
    return RuminaqLaunchDelegate.isRunning();
  }
}
