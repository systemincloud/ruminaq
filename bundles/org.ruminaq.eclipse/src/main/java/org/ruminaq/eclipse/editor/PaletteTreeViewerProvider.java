package org.ruminaq.eclipse.editor;

import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

public class PaletteTreeViewerProvider extends PaletteViewerProvider {

	public PaletteTreeViewerProvider(DiagramBehavior diagramBehavior) {
		super(diagramBehavior.getEditDomain());
	}

}
