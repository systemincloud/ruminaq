package org.ruminaq.launch.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

public class RuminaqLaunchConfigurationTabGroup
    extends AbstractLaunchConfigurationTabGroup {

  public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
    List<ILaunchConfigurationTab> tabs = new ArrayList<>();

    tabs.add(new JavaArgumentsTab());
    tabs.add(new JavaJRETab());
    tabs.add(new JavaClasspathTab());
    tabs.add(new CommonTab());

    setTabs(tabs.toArray(new ILaunchConfigurationTab[tabs.size()]));
  }

}
