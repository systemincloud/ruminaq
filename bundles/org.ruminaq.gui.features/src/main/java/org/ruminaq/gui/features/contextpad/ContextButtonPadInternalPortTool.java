package org.ruminaq.gui.features.contextpad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.tb.IContextButtonEntry;
import org.ruminaq.gui.features.tools.AbstractContextButtonPadTool;

public class ContextButtonPadInternalPortTool extends AbstractContextButtonPadTool {

	public ContextButtonPadInternalPortTool(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public Collection<IContextButtonEntry> getContextButtonPad(IPictogramElementContext context) {
		List<IContextButtonEntry> contexts = new ArrayList<>();
		return contexts;
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
