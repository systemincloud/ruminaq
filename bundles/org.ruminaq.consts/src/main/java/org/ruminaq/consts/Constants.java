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

  public static final String VALIDATION_MARKER = "org.ruminaq.validation.marker"; //$NON-NLS-1$

  public static final String DIAGRAM_EDITOR_ID = "org.ruminaq.eclipse.editor.ruminaqEditor"; //$NON-NLS-1$
  public static final String TEST_DIAGRAM_EDITOR_ID = "org.ruminaq.eclipse.editor.ruminaqEditorTest"; //$NON-NLS-1$

  public static final String INTERNAL_PORT = "internal-port"; //$NON-NLS-1$
  public static final String PORT_LABEL_PROPERTY = "port-label"; //$NON-NLS-1$

  public static final String CAN_DELETE = "can-delete"; //$NON-NLS-1$

  public static final String INF = "inf"; //$NON-NLS-1$
  public static final String M2_HOME = "M2_HOME"; //$NON-NLS-1$

  private Constants() {
  }

}
