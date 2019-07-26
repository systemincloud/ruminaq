package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ruminaq.gui.features.RuminaqFeature;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.model.ruminaq.Port;

@RuminaqFeature(OutputPort.class)
public class AddOutputPortFeature extends AddPortFeature {

	public AddOutputPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof OutputPort
		    && context.getTargetContainer() instanceof Diagram)
			return true;
		else
			return false;
	}

	@Override
	protected int getWidth() {
		return 2;
	}

	@Override
	protected LineStyle getLineStyle(Port port) {
		return LineStyle.SOLID;
	}
}
