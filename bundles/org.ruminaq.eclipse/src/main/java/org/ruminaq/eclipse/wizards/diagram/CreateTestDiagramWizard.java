/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.diagram;

import org.ruminaq.eclipse.Messages;

/**
 * Creates new Ruminaq test diagram.
 *
 * @author Marek Jagielski
 */
public class CreateTestDiagramWizard extends CreateDiagramWizard {

  public static final String ID = CreateTestDiagramWizard.class
      .getCanonicalName();

  /**
   * Sets the window title.
   *
   * @see org.eclipse.jface.wizard.Wizard#setWindowTitle()
   */
  @Override
  public void setWindowTitle(final String newTitle) {
    super.setWindowTitle(Messages.createTestDiagramWizardTitle);
  }

  @Override
  public void addPages() {
    addPage(new CreateTestDiagramWizardNamePage(getSelection()));
  }
}
