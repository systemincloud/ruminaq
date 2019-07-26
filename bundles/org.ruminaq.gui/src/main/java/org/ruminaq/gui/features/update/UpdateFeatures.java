package org.ruminaq.gui.features.update;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.UpdateFeatureExtension;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.MainTask;

@Component(property = { "service.ranking:Integer=5" })
public class UpdateFeatures implements UpdateFeatureExtension {

	@Override
	public List<Class<? extends IUpdateFeature>> getFeatures() {
		return Arrays.asList(UpdateLabelFeature.class, UpdateMainTaskFeature.class,
		    UpdateInputPortFeature.class, UpdateBaseElementFeature.class);
	}

	@Override
	public Predicate<? super Class<? extends IUpdateFeature>> filter(
	    IContext context, IFeatureProvider fp) {
		IUpdateContext updateContext = (IUpdateContext) context;
		PictogramElement pe = updateContext.getPictogramElement();
		Object bo = fp.getBusinessObjectForPictogramElement(pe);
		return (Class<?> clazz) -> {
			if (clazz.isAssignableFrom(UpdateLabelFeature.class)) {
				if (pe instanceof ContainerShape) {
					ContainerShape cs = (ContainerShape) updateContext
					    .getPictogramElement();
					String labelProperty = Graphiti.getPeService().getPropertyValue(cs,
					    Constants.LABEL_PROPERTY);
					return Boolean.parseBoolean(labelProperty);
				}
			} else if (clazz.isAssignableFrom(UpdateMainTaskFeature.class)) {
				return bo instanceof MainTask;
			} else if (clazz.isAssignableFrom(UpdateInputPortFeature.class)) {
				return bo instanceof InputPort;
			} else if (clazz.isAssignableFrom(UpdateBaseElementFeature.class)) {
				return bo instanceof BaseElement;
			}

			return true;
		};
	}
}
