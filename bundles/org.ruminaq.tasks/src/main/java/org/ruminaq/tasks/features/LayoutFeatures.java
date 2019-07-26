package org.ruminaq.tasks.features;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.LayoutFeatureExtension;
import org.ruminaq.model.ruminaq.Task;

@Component(property = { "service.ranking:Integer=10" })
public class LayoutFeatures implements LayoutFeatureExtension {

	public class LayoutTaskFeature extends AbstractLayoutFeature {

		public LayoutTaskFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canLayout(ILayoutContext context) {
			return true;
		}

		@Override
		public boolean layout(ILayoutContext context) {
			return true;
		}
	}

	@Override
	public List<Class<? extends ILayoutFeature>> getFeatures() {
		return Arrays.asList(LayoutTaskFeature.class);
	}

	@Override
	public Predicate<? super Class<? extends ILayoutFeature>> filter(
	    IContext context, IFeatureProvider fp) {
		ILayoutContext layoutContext = (ILayoutContext) context;
		PictogramElement pe = layoutContext.getPictogramElement();
		Object bo = fp.getBusinessObjectForPictogramElement(pe);

		return (Class<?> clazz) -> {
			if (clazz.isAssignableFrom(LayoutTaskFeature.class)) {
				return bo instanceof Task;
			}
			return true;
		};
	}
}
