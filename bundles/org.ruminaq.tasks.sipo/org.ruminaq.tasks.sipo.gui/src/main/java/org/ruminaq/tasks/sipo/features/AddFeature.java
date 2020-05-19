package org.ruminaq.tasks.sipo.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.sipo.Images;
import org.ruminaq.tasks.sipo.impl.Port;

public class AddFeature extends AbstractAddTaskFeature {

  public AddFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected int getHeight() {
    return 50;
  }

  @Override
  protected int getWidth() {
    return 200;
  }

  @Override
  protected String getInsideIconId() {
    return Images.IMG_SIPO_DIAGRAM;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }
}
