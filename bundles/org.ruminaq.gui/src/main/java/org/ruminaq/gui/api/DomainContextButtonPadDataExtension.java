package org.ruminaq.gui.api;

import java.util.Collection;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.tb.IContextButtonEntry;

public interface DomainContextButtonPadDataExtension {

	Collection<? extends IContextButtonEntry> getContextButtonPad(
	    IFeatureProvider fp, IPictogramElementContext context);

}
