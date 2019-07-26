package org.ruminaq.gui.features.copy;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.CopyFeatureExtension;
import org.ruminaq.model.ruminaq.BaseElement;

@Component(property = { "service.ranking:Integer=5" })
public class CopyFeatures implements CopyFeatureExtension {
	
	@Override
	public List<Class<? extends ICopyFeature>> getFeatures() {
		return Arrays.asList(CopyElementFeature.class);
	}
	
	@Override
	public Predicate<? super Class<? extends ICopyFeature>> filter(
	    IContext context, IFeatureProvider fp) {
		ICopyContext addContext = (ICopyContext) context;
		PictogramElement[] pes = addContext.getPictogramElements();
		return (Class<?> clazz) -> {
			for (PictogramElement pe : pes) {
				if (pe instanceof Shape && Graphiti.getPeService().getPropertyValue(pe,
				    Constants.SIMPLE_CONNECTION_POINT) != null) {
					continue;
				}
				Object bo = fp.getBusinessObjectForPictogramElement(pe);
				if (!(bo instanceof BaseElement)) {
					return false;
				}
			}
			return true;
		};
	}
}
