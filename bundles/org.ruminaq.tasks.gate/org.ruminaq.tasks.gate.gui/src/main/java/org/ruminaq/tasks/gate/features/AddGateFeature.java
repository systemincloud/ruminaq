package org.ruminaq.tasks.gate.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.add.AddTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.gate.Port;

public abstract class AddGateFeature extends AddTaskFeature {

  public AddGateFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected int getHeight() {
    return 50;
  }

  @Override
  protected int getWidth() {
    return 50;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }
}
