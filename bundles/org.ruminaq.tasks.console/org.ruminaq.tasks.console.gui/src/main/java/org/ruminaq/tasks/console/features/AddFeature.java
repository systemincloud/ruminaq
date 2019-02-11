package org.ruminaq.tasks.console.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.console.Images;
import org.ruminaq.tasks.console.impl.Port;
import org.ruminaq.tasks.features.AddTaskFeature;

public class AddFeature extends AddTaskFeature {

	public AddFeature(IFeatureProvider fp) { super(fp); }
	
	@Override protected int                         getHeight()           { return 50; }
	@Override protected int                         getWidth()            { return 50; }
	@Override protected boolean                     useIconInsideShape()  { return true; }
	@Override protected String                      getInsideIconId()     { return Images.K.IMG_CONSOLE_DIAGRAM.name(); }
	@Override protected Class<? extends PortsDescr> getPortsDescription() { return Port.class; }
}
