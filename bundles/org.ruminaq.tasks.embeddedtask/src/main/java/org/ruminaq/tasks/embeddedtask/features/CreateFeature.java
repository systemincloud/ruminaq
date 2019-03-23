package org.ruminaq.tasks.embeddedtask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.tasks.TaskCategory;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.tasks.embeddedtask.Images;
import org.ruminaq.tasks.embeddedtask.Port;
import org.ruminaq.tasks.features.CreateTaskFeature;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {
		super(fp, EmbeddedTask.class, bundleName, version);
	}

	@Override
	public String getPaletteKey() {
		return SicPlugin.GUI_ID.s();
	}

	@Override
	public String getTestPaletteKey() {
		return SicPlugin.GUI_ID.s();
	}

	@Override
	public String getTaskCategory() {
		return TaskCategory.USERDEFINED.name();
	}

	@Override
	public String getTestTaskCategory() {
		return TaskCategory.USERDEFINED.name();
	}

	@Override
	public Object[] create(ICreateContext context) {
		return super.create(context, RuminaqFactory.eINSTANCE.createEmbeddedTask());
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}

	@Override
	public String getCreateImageId() {
		return Images.K.IMG_EMBEDDEDTASK_PALETTE.name();
	}
}
