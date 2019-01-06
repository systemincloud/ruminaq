package org.ruminaq.tasks.embeddedtask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.embeddedtask.Images;
import org.ruminaq.tasks.embeddedtask.Port;
import org.ruminaq.tasks.features.AddTaskFeature;

public class AddFeature extends AddTaskFeature {

	public static String NOT_CHOSEN = "???";

	public AddFeature(IFeatureProvider fp) { super(fp); }

	@Override protected boolean                     useIconInsideShape()  { return true; }
	@Override protected String                      getInsideIconId()     { return Images.K.IMG_EMBEDDEDTASK_DIAGRAM_MAIN.name(); }
	@Override protected String                      getInsideIconDesc()   { return NOT_CHOSEN; }
	@Override protected Class<? extends PortsDescr> getPortsDescription() { return Port.class; }
}
