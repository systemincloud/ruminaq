/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.diagram;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.ruminaq.eclipse.Messages;

/**
 *
 * @author Marek Jagielski
 */
public class CreateTestDiagramWizard extends CreateDiagramWizard {

  public static final String ID = CreateTestDiagramWizard.class.getCanonicalName();

  @Override
  public void init(IWorkbench workbench,
      IStructuredSelection currentSelection) {
    super.init(workbench, currentSelection);
    setWindowTitle(Messages.createTestDiagramWizardTitle);
  }

  @Override
  public void addPages() {
    page = new CreateTestDiagramWizardNamePage(getSelection());
    addPage(page);
  }
}
