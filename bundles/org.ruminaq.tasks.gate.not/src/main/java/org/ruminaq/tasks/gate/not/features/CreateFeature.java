package org.ruminaq.tasks.gate.not.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.TaskCategory;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.gate.not.Images;
import org.ruminaq.tasks.gate.not.impl.Port;
import org.ruminaq.tasks.gate.not.model.not.Not;
import org.ruminaq.tasks.gate.not.model.not.NotFactory;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {
		super(fp, Not.class, bundleName, version);
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
		return TaskCategory.LOGIC.name();
	}

	@Override
	public String getTestTaskCategory() {
		return TaskCategory.LOGIC.name();
	}

	@Override
	public Object[] create(ICreateContext context) {
		Not gt = NotFactory.eINSTANCE.createNot();
		gt.setInputNumber(1);
		return super.create(context, gt);
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}

	@Override
	public String getCreateImageId() {
		return Images.K.IMG_NOT_PALETTE.name();
	}
}
