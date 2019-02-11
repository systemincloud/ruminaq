package org.ruminaq.tasks.demux.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.demux.Images;
import org.ruminaq.tasks.demux.impl.Port;
import org.ruminaq.tasks.features.AddTaskFeature;

public class AddFeature extends AddTaskFeature {

	public AddFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected int getHeight() {
		return 55;
	}

	@Override
	protected int getWidth() {
		return 55;
	}

	@Override
	protected boolean useIconInsideShape() {
		return true;
	}

	@Override
	protected String getInsideIconId() {
		return Images.K.IMG_DEMUX_DIAGRAM.name();
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}
}
