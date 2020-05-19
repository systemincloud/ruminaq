package org.ruminaq.tasks.gate.not;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.gate.features.AddGateFeature;
import org.ruminaq.tasks.gate.not.impl.Port;

public class AddFeature extends AddGateFeature {

  public AddFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected String getInsideIconId() {
    return Images.IMG_NOT_DIAGRAM;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }
}
