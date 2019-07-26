package org.ruminaq.gui.features.tools;

import java.util.Collection;

import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.tb.IContextButtonEntry;

public interface IContextButtonPadTool {

	public Collection<IContextButtonEntry> getContextButtonPad(IPictogramElementContext context);
	
	public int getGenericContextButtons();

	public IRectangle getPadLocation(IRectangle rectangle);
	
}
