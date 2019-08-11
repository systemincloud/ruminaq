package org.ruminaq.tasks.pythontask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.pythontask.Images;
import org.ruminaq.tasks.pythontask.Port;
import org.ruminaq.tasks.pythontask.model.pythontask.PythonTask;
import org.ruminaq.tasks.pythontask.model.pythontask.PythontaskFactory;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {
		super(fp, PythonTask.class, bundleName, version);
	}

	@Override
	public Object[] create(ICreateContext context) {
		return super.create(context, PythontaskFactory.eINSTANCE.createPythonTask());
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}

	@Override
	public String getCreateImageId() {
		return Images.K.IMG_PYTHONTASK_PALETTE.name();
	}
}
