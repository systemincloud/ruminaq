package org.ruminaq.gui.features.custom;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CustomFeaturesExtension;
import org.ruminaq.gui.features.create.CreateSimpleConnectionPointFeature;

@Component(property = { "service.ranking:Integer=5" })
public class CustomFeatures implements CustomFeaturesExtension {

	@Override
	public List<Class<? extends ICustomFeature>> getFeatures() {
		return Arrays.asList(CreateSimpleConnectionPointFeature.class);
	}
}
