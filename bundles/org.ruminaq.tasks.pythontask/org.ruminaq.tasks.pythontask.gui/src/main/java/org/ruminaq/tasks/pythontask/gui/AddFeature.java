package org.ruminaq.tasks.pythontask.gui;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;
import org.ruminaq.model.desc.PortsDescr;

public class AddFeature extends AbstractAddTaskFeature {

  public static String NOT_CHOSEN = "???";

  public AddFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected String getInsideIconId() {
    return Images.IMG_PYTHONTASK_DIAGRAM;
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
