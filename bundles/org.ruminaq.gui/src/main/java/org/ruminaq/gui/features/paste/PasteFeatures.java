package org.ruminaq.gui.features.paste;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.PasteFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class PasteFeatures implements PasteFeatureExtension {

	@Override
	public List<Class<? extends IPasteFeature>> getFeatures() {
		return Arrays.asList(PasteElementFeature.class);
	}

	@Override
	public Predicate<? super Class<? extends IPasteFeature>> filter(
	    IContext context, IFeatureProvider fp) {
		IPasteContext addContext = (IPasteContext) context;
		return (Class<?> clazz) -> {
			return true;
		};
	}
}
