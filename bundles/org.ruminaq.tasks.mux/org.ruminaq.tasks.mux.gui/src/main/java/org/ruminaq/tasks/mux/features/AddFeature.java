package org.ruminaq.tasks.mux.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.mux.Images;
import org.ruminaq.tasks.mux.impl.Port;

public class AddFeature extends AbstractAddTaskFeature {

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
    return Images.IMG_MUX_DIAGRAM;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }
}
