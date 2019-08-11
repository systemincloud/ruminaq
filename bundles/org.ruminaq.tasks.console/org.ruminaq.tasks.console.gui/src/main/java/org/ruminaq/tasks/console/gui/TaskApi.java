/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.console.gui;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.ruminaq.launch.LaunchListener;
import org.ruminaq.launch.RuminaqLaunchDelegate;
import org.ruminaq.runner.dirmi.DirmiServer;
import org.ruminaq.tasks.api.ITaskApi;

@Component
public class TaskApi implements ITaskApi, LaunchListener {

  private String symbolicName;
  private Version version;

  @Activate
  void activate(Map<String, Object> properties) {
    Bundle b = FrameworkUtil.getBundle(getClass());
    symbolicName = b.getSymbolicName();
    version = b.getVersion();
    RuminaqLaunchDelegate.addLaunchListener(this);
  }

  @Deactivate
  public void deactivate() {
    RuminaqLaunchDelegate.removeLaunchListener(this);
  }

  @Override
  public void dirmiStarted() {
    DirmiServer.INSTANCE.createSessionAcceptor(
        symbolicName + ":" + version.getMajor() + "." + version.getMinor() + "."
            + version.getMicro(),
        this.getClass().getClassLoader());
  }
}
