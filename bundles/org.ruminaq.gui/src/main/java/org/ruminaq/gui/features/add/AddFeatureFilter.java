package org.ruminaq.gui.features.add;

import java.util.function.Predicate;

import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.ruminaq.model.ruminaq.BaseElement;

public abstract class AddFeatureFilter implements Predicate<IContext> {

	@Override
	public boolean test(IContext context) {
		IAddContext addContext = (IAddContext) context;
		return forBusinessObject().isAssignableFrom(addContext.getNewObject().getClass());
	}
	
	public abstract Class<? extends BaseElement> forBusinessObject();
}
