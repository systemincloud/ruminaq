/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.diagram;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.wizards.project.CreateSourceFolders;

/**
 * Create new Test Diagram page.
 *
 * @author Marek Jagielski
 */
public class CreateTestDiagramWizardNamePage
    extends CreateDiagramWizardNamePage {

  public static final String DEFAULT_DIAGRAM_NAME = "MyTaskTest";

  /**
   * Create new Test Diagram page entry constructor.
   *
   */
  public CreateTestDiagramWizardNamePage(IStructuredSelection selection) {
    super(selection);
    super.setTitle(Messages.createTestDiagramWizardTitle);
    super.setDescription(Messages.createTestDiagramWizardDescription);
  }

  @Override
  protected String getResourceFolder() {
    return CreateSourceFolders.TEST_RESOURCES;
  }

  @Override
  protected String getDiagramFolder() {
    return CreateSourceFolders.TEST_DIAGRAM_FOLDER;
  }

  @Override
  protected String getDefaultName() {
    return DEFAULT_DIAGRAM_NAME + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT;
  }
}
