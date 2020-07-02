/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tests.common.reddeer;

import org.eclipse.reddeer.eclipse.selectionwizard.NewMenuWizard;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

public class RuminaqDiagramWizard extends NewMenuWizard {

  public RuminaqDiagramWizard() {
    super("New Diagram", "Ruminaq", "Ruminaq Diagram");
  }

  public void create(String projectName, String diagramFolder, String diagramName) {
    open();
    new LabeledText("Project:").setText(projectName);
    new LabeledText("Container:").setText(diagramFolder);
    new LabeledText("File name:").setText(diagramName);
    finish();
  }
}
