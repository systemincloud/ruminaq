package org.ruminaq.eclipse.editor;

import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

public class RuminaqPaletteBehavior extends DefaultPaletteBehavior {

  public RuminaqPaletteBehavior(DiagramBehavior diagramBehavior) {
    super(diagramBehavior);
  }

  @Override
  protected PaletteViewerProvider createPaletteViewerProvider() {
    return super.createPaletteViewerProvider();
  }
}
