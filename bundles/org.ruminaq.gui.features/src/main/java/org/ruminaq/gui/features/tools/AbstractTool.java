package org.ruminaq.gui.features.tools;

import org.eclipse.graphiti.features.IFeatureProvider;

public class AbstractTool {

	private IFeatureProvider fp;
	
	public AbstractTool(IFeatureProvider fp) {
		this.fp = fp;
	}
	
	protected IFeatureProvider getFeatureProvider() {
		return fp;
	}

}
