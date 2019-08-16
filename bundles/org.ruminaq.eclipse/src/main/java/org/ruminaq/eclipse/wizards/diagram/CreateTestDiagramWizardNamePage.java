/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.diagram;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.wizards.project.SourceFolders;

/**
 *
 * @author Marek Jagielski
 */
public class CreateTestDiagramWizardNamePage
    extends CreateDiagramWizardNamePage {

  public static final String DEFAULT_DIAGRAM_NAME = "MyTaskTest";

  public CreateTestDiagramWizardNamePage(IStructuredSelection selection) {
    super(selection);
    setTitle(Messages.createTestDiagramWizardTitle);
    setDescription(Messages.createTestDiagramWizardDescription);
  }

  @Override
  protected String getResourceFolder() {
    return SourceFolders.TEST_RESOURCES;
  }

  @Override
  protected String getDiagramFolder() {
    return SourceFolders.TEST_DIAGRAM_FOLDER;
  }

  @Override
  protected String getDefaultName() {
    return DEFAULT_DIAGRAM_NAME + Constants.DIAGRAM_EXTENSION_DOT;
  }
}
