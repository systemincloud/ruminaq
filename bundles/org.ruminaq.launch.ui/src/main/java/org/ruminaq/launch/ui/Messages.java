/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.launch.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

  private static final String BUNDLE_NAME = "org.ruminaq.launch.ui.messages"; //$NON-NLS-1$
  public static String RuminaqLaunchConfigurationTab_tab_label;
  public static String RuminaqLaunchConfigurationTab_label_project;
  public static String RuminaqLaunchConfigurationTab_projectdialog_title;
  public static String RuminaqLaunchConfigurationTab_label_browse;
  public static String RuminaqLaunchConfigurationTab_label_test;
  public static String RuminaqLaunchConfigurationTab_testdialog_title;
  public static String RuminaqLaunchConfigurationTab_label_machine;
  public static String RuminaqLaunchConfigurationTab_error_projectnotdefined;
  public static String RuminaqLaunchConfigurationTab_error_testnotdefined;
  public static String RuminaqLaunchConfigurationTab_warning_machinenotdefined;

  static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}
