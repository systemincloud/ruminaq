/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.embeddedtask.ui.properties;

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
