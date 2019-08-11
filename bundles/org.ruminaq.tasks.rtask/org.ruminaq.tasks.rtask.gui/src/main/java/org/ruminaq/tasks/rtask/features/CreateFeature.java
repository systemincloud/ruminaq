package org.ruminaq.tasks.rtask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.rtask.Images;
import org.ruminaq.tasks.rtask.Port;
import org.ruminaq.tasks.rtask.model.rtask.RtaskFactory;
import org.ruminaq.tasks.rtask.model.rtask.RTask;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {
		super(fp, RTask.class, bundleName, version);
	}

	@Override
	public Object[] create(ICreateContext context) {
		return super.create(context, RtaskFactory.eINSTANCE.createRTask());
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}

	@Override
	public String getCreateImageId() {
		return Images.K.IMG_RTASK_PALETTE.name();
	}
}
