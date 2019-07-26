package org.ruminaq.gui.features.directediting;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.DirectEditingFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class DirectEditingFeatures implements DirectEditingFeatureExtension {

	@Override
	public List<Class<? extends IDirectEditingFeature>> getFeatures() {
		return Arrays.asList(DirectEditLabelFeature.class);
	}
}
