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
