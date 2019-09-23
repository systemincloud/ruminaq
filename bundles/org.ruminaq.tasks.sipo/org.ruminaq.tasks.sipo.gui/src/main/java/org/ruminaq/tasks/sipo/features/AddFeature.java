package org.ruminaq.tasks.sipo.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.features.AddTaskFeature;
import org.ruminaq.tasks.sipo.Images;
import org.ruminaq.tasks.sipo.impl.Port;

public class AddFeature extends AddTaskFeature {

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
  protected boolean useIconInsideShape() {
    return true;
  }

  @Override
  protected String getInsideIconId() {
    return Images.K.IMG_SIPO_DIAGRAM.name();
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }
}
