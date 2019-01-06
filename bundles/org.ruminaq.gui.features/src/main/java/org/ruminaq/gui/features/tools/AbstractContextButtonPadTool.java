package org.ruminaq.gui.features.tools;

import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.features.IFeatureProvider;

public abstract class AbstractContextButtonPadTool extends AbstractTool implements IContextButtonPadTool {

	public AbstractContextButtonPadTool(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public int getGenericContextButtons() {
		return -1;
	}
	
	@Override
	public IRectangle getPadLocation(IRectangle rectangle) {
		return null;
	}

}
