package org.ruminaq.tasks.embeddedtask.ui.properties;

import org.osgi.framework.FrameworkUtil;
import org.ruminaq.launch.LaunchListener;
import org.ruminaq.launch.RuminaqLaunchDelegate;
import org.ruminaq.tasks.userdefined.properties.UserDefinedParametersUiSection;

public class ParametersSection extends UserDefinedParametersUiSection {

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
}
