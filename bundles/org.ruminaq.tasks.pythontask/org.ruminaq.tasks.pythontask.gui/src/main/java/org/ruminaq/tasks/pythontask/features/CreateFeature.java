package org.ruminaq.tasks.pythontask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.TaskCategory;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.pythontask.Images;
import org.ruminaq.tasks.pythontask.Port;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {	super(fp, PythonTask.class, bundleName, version); }

	@Override public String getPaletteKey()       { return SicPlugin.GUI_ID.s(); }
	@Override public String getTestPaletteKey()   { return SicPlugin.GUI_ID.s(); }
	@Override public String getTaskCategory()     { return TaskCategory.USERDEFINED.name(); }
	@Override public String getTestTaskCategory() { return TaskCategory.USERDEFINED.name(); }

	@Override public    Object[]                    create(ICreateContext context) { return super.create(context, PythontaskFactory.eINSTANCE.createPythonTask()); }
	@Override protected Class<? extends PortsDescr> getPortsDescription()          { return Port.class; }
	@Override public    String                      getCreateImageId()             { return Images.K.IMG_PYTHONTASK_PALETTE.name(); }
}
