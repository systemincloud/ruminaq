/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.launch;

public interface RuminaqLaunchConfigurationConstants {

  static final String ECLIPSE_ID = "org.ruminaq.eclipse";

  public static final String ATTR_PROJECT_NAME = ECLIPSE_ID + ".PROJECT_ATTR"; //$NON-NLS-1$
  public static final String ATTR_TEST_TASK = ECLIPSE_ID + ".TEST_TASK_ATTR"; //$NON-NLS-1$
  public static final String ATTR_MACHINE_ID = ECLIPSE_ID + ".MACHINE_ID_ATTR"; //$NON-NLS-1$
  public static final String ATTR_ONLY_LOCAL_TASKS = ECLIPSE_ID
      + ".ONLY_LOCAL_TASKS_ATTR"; //$NON-NLS-1$

}
