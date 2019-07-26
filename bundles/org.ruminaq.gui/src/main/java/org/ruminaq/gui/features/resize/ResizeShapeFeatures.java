package org.ruminaq.gui.features.resize;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ResizeShapeFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class ResizeShapeFeatures implements ResizeShapeFeatureExtension {

	@Override
	public List<Class<? extends IResizeShapeFeature>> getFeatures() {
		return Arrays.asList(ResizeShapeForbiddenFeature.class);
	}
}
