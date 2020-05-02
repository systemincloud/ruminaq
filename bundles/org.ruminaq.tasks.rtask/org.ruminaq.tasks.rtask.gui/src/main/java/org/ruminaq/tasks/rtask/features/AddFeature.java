package org.ruminaq.tasks.rtask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.rtask.Images;
import org.ruminaq.tasks.rtask.Port;

public class AddFeature extends AbstractAddTaskFeature {

  public static String NOT_CHOSEN = "???";

  public AddFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected boolean useIconInsideShape() {
    return true;
  }

  @Override
  protected String getInsideIconId() {
    return Images.IMG_RTASK_DIAGRAM;
  }

  @Override
  protected String getInsideIconDesc() {
    return NOT_CHOSEN;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }
}
