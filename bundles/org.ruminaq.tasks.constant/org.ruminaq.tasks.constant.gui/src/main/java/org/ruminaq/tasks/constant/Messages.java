/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

  private static final String BUNDLE_NAME = "org.ruminaq.tasks.constant.messages"; //$NON-NLS-1$
  public static String Properties_Constant_label_type;
  public static String Properties_Constant_label_value;
  public static String PropertyConstantTaskSection_valueNumericText_text;

  static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}
