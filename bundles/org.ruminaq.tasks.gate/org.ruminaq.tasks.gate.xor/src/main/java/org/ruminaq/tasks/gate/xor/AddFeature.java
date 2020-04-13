package org.ruminaq.tasks.gate.xor;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.tasks.gate.features.AddGateFeature;

public class AddFeature extends AddGateFeature {

  public AddFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected boolean useIconInsideShape() {
    return true;
  }

  @Override
  protected String getInsideIconId() {
    return Images.IMG_XOR_DIAGRAM;
  }
}
