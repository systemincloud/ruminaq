/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import org.eclipse.osgi.util.NLS;
import org.ruminaq.util.PlatformUtil;

/**
 * Messages.
 *
 * @author Marek Jagielski
 */
public final class Messages extends NLS {

  public static String createJavaTaskWizardName;
  public static String customNewClassWizardPageDescription;
  public static String propertySectionSetCommand;

  static {
    NLS.initializeMessages(
        PlatformUtil.getBundleSymbolicName(Messages.class) + ".messages",
        Messages.class);
  }

  private Messages() {
  }
}
