package org.ruminaq.eclipse.editor;

import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;

public class RuminaqDiagramBehavior extends DiagramBehavior {

  public RuminaqDiagramBehavior(IDiagramContainerUI diagramContainer) {
    super(diagramContainer);
  }

  @Override
  protected DefaultPaletteBehavior createPaletteBehaviour() {
    return new RuminaqPaletteBehavior(this);
  }
}
