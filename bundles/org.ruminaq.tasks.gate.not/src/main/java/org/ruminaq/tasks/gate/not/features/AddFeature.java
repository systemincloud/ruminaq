package org.ruminaq.tasks.gate.not.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.gate.features.AddGateFeature;
import org.ruminaq.tasks.gate.not.Images;
import org.ruminaq.tasks.gate.not.impl.Port;

public class AddFeature extends AddGateFeature {

	public AddFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected boolean useIconInsideShape() {
		return true;
	}

	@Override
	protected String getInsideIconId() {
		return Images.K.IMG_NOT_DIAGRAM.name();
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}
}
