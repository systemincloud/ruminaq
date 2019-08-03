package org.ruminaq.gui.features.contextbuttonpad;

import java.util.function.Predicate;

import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ContextButtonPadLocationExtension;
import org.ruminaq.gui.api.GenericContextButtonPadDataExtension;
import org.ruminaq.gui.features.contextbuttonpad.ContextButtonPadDataLabelFeature.Filter;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class ContextButtonPadInternalPortTool implements
    GenericContextButtonPadDataExtension, ContextButtonPadLocationExtension {

	static class Filter implements Predicate<ServiceFilterArgs> {

		@Override
		public boolean test(ServiceFilterArgs args) {
			IFeatureProvider fp = (IFeatureProvider) args.getArgs().get(0);
			IPictogramElementContext context = (IPictogramElementContext) args
			    .getArgs().get(1);
			PictogramElement pe = context.getPictogramElement();
			return fp
			    .getBusinessObjectForPictogramElement(pe) instanceof InternalPort;
		}
	}

	@Override
	public int getGenericContextButtons() {
		return 0;
	}

	@Override
	public IRectangle getPadLocation(IRectangle rectangle) {
		rectangle.setHeight(80);
		return rectangle;
	}
}
