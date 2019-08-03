package org.ruminaq.gui.features.paste;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.PasteElementFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class PasteElementFeatures implements PasteElementFeatureExtension {

	@Override
	public List<Class<? extends RuminaqPasteFeature>> getFeatures() {
		return Arrays.asList(PasteInputPortFeature.class,
		    PasteOutputPortFeature.class);
	}
}
