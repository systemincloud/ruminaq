package org.ruminaq.gui.features.directediting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.DirectEditingFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class DirectEditingFeatures implements DirectEditingFeatureExtension {

	@Override
	public List<Class<? extends IDirectEditingFeature>> getFeatures() {
		return Arrays.asList(DirectEditLabelFeature.class);
	}
	
	@Override
	public Predicate<? super Class<? extends IDirectEditingFeature>> filter(
	    IContext context, IFeatureProvider fp) {
		IDirectEditingContext directEditingContext = (IDirectEditingContext) context;
		return (Class<?> clazz) -> {
			if (clazz.isAssignableFrom(DirectEditLabelFeature.class)) {
				String labelProperty = Graphiti.getPeService().getPropertyValue(
						directEditingContext.getPictogramElement(), Constants.LABEL_PROPERTY);
				return Boolean.parseBoolean(labelProperty);
			}
			return true;
		};
	}
}
