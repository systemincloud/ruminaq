package org.ruminaq.gui.features.contextbuttonpad;

import java.util.function.Predicate;

import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.ContextButtonPadLocationExtension;
import org.ruminaq.gui.features.contextbuttonpad.ContextButtonPadConnectionPointTool.Filter;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class ContextButtonPadConnectionPointTool implements ContextButtonPadLocationExtension {

	static class Filter implements Predicate<ServiceFilterArgs> {

		@Override
		public boolean test(ServiceFilterArgs args) {
			IPictogramElementContext context = (IPictogramElementContext) args
			    .getArgs().get(1);
			PictogramElement pe = context.getPictogramElement();
			return Boolean
			    .parseBoolean(Graphiti.getPeService().getPropertyValue(pe,
			        Constants.SIMPLE_CONNECTION_POINT));
		}
	}
	
	@Override
	public IRectangle getPadLocation(IRectangle rectangle) {
		rectangle.setHeight(80);
		return rectangle;
	}
}
