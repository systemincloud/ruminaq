package org.ruminaq.tasks.gate.xor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.tasks.gate.features.AddGateFeature;
import org.ruminaq.tasks.gate.xor.Images;

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
		return Images.K.IMG_XOR_DIAGRAM.name();
	}
}
