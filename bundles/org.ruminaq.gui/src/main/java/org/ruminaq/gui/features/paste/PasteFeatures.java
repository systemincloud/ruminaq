package org.ruminaq.gui.features.paste;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.IPasteFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.PasteFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class PasteFeatures implements PasteFeatureExtension {

	@Override
	public List<Class<? extends IPasteFeature>> getFeatures() {
		return Arrays.asList(PasteElementFeature.class);
	}
}
