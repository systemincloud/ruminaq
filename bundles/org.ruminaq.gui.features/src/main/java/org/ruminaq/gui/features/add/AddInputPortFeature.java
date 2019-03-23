package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.Port;

public class AddInputPortFeature extends AddPortFeature {

	public AddInputPortFeature(IFeatureProvider fp) { super(fp); }

	@Override
	public boolean canAdd(IAddContext context) {
	    if (   context.getNewObject()       instanceof InputPort
		    && context.getTargetContainer() instanceof Diagram) return true;
        else return false;
	}

	@Override protected int getWidth() { return 1; }

	@Override
	protected LineStyle getLineStyle(Port port) {
		if(((InputPort)port).isAsynchronous()) return LineStyle.DOT;
		else                                   return LineStyle.SOLID;
	}
}
