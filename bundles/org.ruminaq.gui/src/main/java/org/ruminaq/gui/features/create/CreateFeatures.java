package org.ruminaq.gui.features.create;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CreateFeaturesExtension;

@Component(property = { "service.ranking:Integer=5" })
public class CreateFeatures implements CreateFeaturesExtension {

	@Override
	public List<Class<? extends ICreateFeature>> getCreateFeatures() {
		return Arrays.asList(CreateInputPortFeature.class,
		    CreateOutputPortFeature.class);
	}
}
