package org.ruminaq.gui.features.add;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.AddFeatureExtension;
import org.ruminaq.gui.features.RuminaqFeature;

@Component(property = { "service.ranking:Integer=5" })
public class AddFeatures implements AddFeatureExtension {

	@Override
	public List<Class<? extends IAddFeature>> getFeatures() {
		return Arrays.asList(AddInputPortFeature.class, AddOutputPortFeature.class,
		    AddSimpleConnectionFeature.class);
	}

	@Override
	public Predicate<? super Class<? extends IAddFeature>> filter(
	    IContext context, IFeatureProvider fp) {
		IAddContext addContext = (IAddContext) context;
		return (Class<?> clazz) -> {
			return Optional.ofNullable(clazz.getAnnotation(RuminaqFeature.class))
			    .map(RuminaqFeature::value)
			    .map(c -> c.isAssignableFrom(addContext.getNewObject().getClass()))
			    .orElse(false);
		};
	}
}
