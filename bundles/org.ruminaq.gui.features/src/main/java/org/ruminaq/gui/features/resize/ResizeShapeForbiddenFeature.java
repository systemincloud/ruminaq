package org.ruminaq.gui.features.resize;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;

public class ResizeShapeForbiddenFeature extends DefaultResizeShapeFeature {

	public ResizeShapeForbiddenFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		return false;
	}
}
