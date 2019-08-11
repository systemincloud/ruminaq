package org.ruminaq.tasks.mux.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.mux.Images;
import org.ruminaq.tasks.mux.impl.Port;
import org.ruminaq.tasks.mux.model.mux.Mux;
import org.ruminaq.tasks.mux.model.mux.MuxFactory;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {
		super(fp, Mux.class, bundleName, version);
	}

	@Override
	public Object[] create(ICreateContext context) {
		return super.create(context, MuxFactory.eINSTANCE.createMux());
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}

	@Override
	public String getCreateImageId() {
		return Images.K.IMG_MUX_PALETTE.name();
	}
}
