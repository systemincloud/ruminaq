package org.ruminaq.tasks.pythontask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.features.AddTaskFeature;
import org.ruminaq.tasks.pythontask.Images;
import org.ruminaq.tasks.pythontask.Port;

public class AddFeature extends AddTaskFeature {

	public static String NOT_CHOSEN = "???";

	public AddFeature(IFeatureProvider fp) { super(fp);	}

	@Override protected boolean                     useIconInsideShape()  { return true; }
	@Override protected String                      getInsideIconId()     { return Images.K.IMG_PYTHONTASK_DIAGRAM.name(); }
	@Override protected String                      getInsideIconDesc()   { return NOT_CHOSEN; }
	@Override protected Class<? extends PortsDescr> getPortsDescription() { return Port.class; }
}
