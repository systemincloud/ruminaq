package org.ruminaq.gui.features.contextpad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.tb.IContextButtonEntry;
import org.ruminaq.gui.features.tools.AbstractContextButtonPadTool;

public final class ContextButtonPadBaseElementTool extends AbstractContextButtonPadTool {

	public ContextButtonPadBaseElementTool(IFeatureProvider fp) {
		super(fp);
	}

	public Collection<IContextButtonEntry> getContextButtonPad(IPictogramElementContext context) {
		List<IContextButtonEntry> contexts = new ArrayList<>();
		return contexts;	
	}

}
