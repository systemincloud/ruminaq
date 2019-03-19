package org.ruminaq.tasks.inspect.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Version;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.TaskCategory;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.inspect.Images;
import org.ruminaq.tasks.inspect.impl.Port;
import org.ruminaq.tasks.inspect.model.inspect.Inspect;
import org.ruminaq.tasks.inspect.model.inspect.InspectFactory;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) { super(fp, Inspect.class, bundleName, version); }

	@Override public String getPaletteKey()       { return SicPlugin.GUI_ID.s(); }
	@Override public String getTestPaletteKey()   { return SicPlugin.GUI_ID.s(); }
	@Override public String getTaskCategory()     { return TaskCategory.TEXT.name(); }
	@Override public String getTestTaskCategory() { return TaskCategory.TEXT.name(); }

	@Override public Object[] create(ICreateContext context) {
		Inspect inspect = InspectFactory.eINSTANCE.createInspect();
		inspect.setOnlyLocal(true);
		return super.create(context, inspect);
	}

	@Override protected Class<? extends PortsDescr> getPortsDescription()          { return Port.class; }
	@Override public    String                      getCreateImageId()             { return Images.K.IMG_INSPECT_PALETTE.name(); }
}
