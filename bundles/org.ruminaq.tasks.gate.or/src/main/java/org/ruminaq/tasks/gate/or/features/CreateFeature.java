package org.ruminaq.tasks.gate.or.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.TaskCategory;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.gate.Port;
import org.ruminaq.tasks.gate.or.Images;
import org.ruminaq.tasks.gate.or.model.or.Or;
import org.ruminaq.tasks.gate.or.model.or.OrFactory;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {
		super(fp, Or.class, bundleName, version);
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
		return super.create(context, OrFactory.eINSTANCE.createOr());
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}

	@Override
	public String getCreateImageId() {
		return Images.K.IMG_OR_PALETTE.name();
	}
}
