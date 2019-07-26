package org.ruminaq.gui.features.add;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.IAddFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.AddFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class AddFeatures implements AddFeatureExtension {

	@Override
	public List<Class<? extends IAddFeature>> getFeatures() {
		return Arrays.asList(AddInputPortFeature.class, AddOutputPortFeature.class,
		    AddSimpleConnectionFeature.class);
	}
}
