package org.ruminaq.tasks.randomgenerator.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.add.AddTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.randomgenerator.Images;
import org.ruminaq.tasks.randomgenerator.impl.Port;

public class AddFeature extends AddTaskFeature {

  public AddFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected int getHeight() {
    return 55;
  }

  @Override
  protected int getWidth() {
    return 55;
  }

  @Override
  protected boolean useIconInsideShape() {
    return true;
  }

  @Override
  protected String getInsideIconId() {
    return Images.K.IMG_RANDOMGENERATOR_DIAGRAM.name();
  }

  @Override
  protected String getInsideIconDesc() {
    return "???";
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }
}
