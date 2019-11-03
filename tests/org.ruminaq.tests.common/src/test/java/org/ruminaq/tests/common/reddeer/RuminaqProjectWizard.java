/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common.reddeer;

import org.eclipse.reddeer.eclipse.selectionwizard.NewMenuWizard;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Create new Ruminaq project.
 *
 * @author Marek Jagielski
 *
 */
public class RuminaqProjectWizard extends NewMenuWizard {

  /**
   * Constructs the wizard with "Ruminaq" - "Project".
   */
  public RuminaqProjectWizard() {
    super("New Project", "Ruminaq", "Project");
  }

  public void create(String name) {
    open();
    new LabeledText("Project name:").setText(name);
    finish();
  }
}
