/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.consts;

public final class Constants {

  public enum SicPlugin {
    CONSTS_ID("org.ruminaq.consts"), ECLIPSE_ID("org.ruminaq.eclipse"),
    GUI_ID("org.ruminaq.gui"), LAUNCH_ID("org.ruminaq.launch"),
    MODEL_ID("org.ruminaq.model"), LOGS_ID("org.ruminaq.logs"),
    RUNNER_ID("org.ruminaq.runner"), UTIL_ID("org.ruminaq.util"),
    TASKS_ID("org.ruminaq.tasks"),;

    private String symbolicName;

    SicPlugin(String symbolicName) {
      this.symbolicName = symbolicName;
    }

    public String s() {
      return this.symbolicName;
    }
  }

  private Constants() {
  }

}
