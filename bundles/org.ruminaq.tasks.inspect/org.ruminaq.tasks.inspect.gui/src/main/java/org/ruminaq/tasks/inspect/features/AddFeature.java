package org.ruminaq.tasks.inspect.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.inspect.Images;
import org.ruminaq.tasks.inspect.impl.Port;

public class AddFeature extends AbstractAddTaskFeature {

  public AddFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected int getHeight() {
    return 30;
  }

  @Override
  protected int getWidth() {
    return 45;
  }

  @Override
  protected boolean useIconInsideShape() {
    return true;
  }

  @Override
  protected String getInsideIconId() {
    return Images.K.IMG_INSPECT_DIAGRAM.name();
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }
}
