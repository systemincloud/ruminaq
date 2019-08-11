package org.ruminaq.tasks.sipo.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.sipo.Images;
import org.ruminaq.tasks.sipo.impl.Port;
import org.ruminaq.tasks.sipo.model.sipo.Sipo;
import org.ruminaq.tasks.sipo.model.sipo.SipoFactory;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) { super(fp, Sipo.class, bundleName, version); }

	@Override
	public Object[] create(ICreateContext context) {
		return super.create(context, SipoFactory.eINSTANCE.createSipo());
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}

	@Override
	public String getCreateImageId() {
		return Images.K.IMG_SIPO_PALETTE.name();
	}
}
