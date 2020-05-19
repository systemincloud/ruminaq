package org.ruminaq.tasks.gate.or;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.tasks.gate.features.AddGateFeature;

public class AddFeature extends AddGateFeature {

  public AddFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected String getInsideIconId() {
    return Images.IMG_OR_DIAGRAM;
  }
}
