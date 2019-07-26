package org.ruminaq.gui.features.create;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CreateConnectionFeaturesExtension;

@Component(property = { "service.ranking:Integer=5" })
public class CreateConnectionFeatures
    implements CreateConnectionFeaturesExtension {

	@Override
	public List<Class<? extends ICreateConnectionFeature>> getFeatures() {
		return Arrays.asList(CreateSimpleConnectionFeature.class);
	}
}
