/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.launch;

import org.ruminaq.consts.Constants.SicPlugin;

public interface RuminaqLaunchConfigurationConstants {

  public static final String ATTR_PROJECT_NAME = SicPlugin.ECLIPSE_ID.s()
      + ".PROJECT_ATTR"; //$NON-NLS-1$
  public static final String ATTR_TEST_TASK = SicPlugin.ECLIPSE_ID.s()
      + ".TEST_TASK_ATTR"; //$NON-NLS-1$
  public static final String ATTR_MACHINE_ID = SicPlugin.ECLIPSE_ID.s()
      + ".MACHINE_ID_ATTR"; //$NON-NLS-1$
  public static final String ATTR_ONLY_LOCAL_TASKS = SicPlugin.ECLIPSE_ID.s()
      + ".ONLY_LOCAL_TASKS_ATTR"; //$NON-NLS-1$

}
