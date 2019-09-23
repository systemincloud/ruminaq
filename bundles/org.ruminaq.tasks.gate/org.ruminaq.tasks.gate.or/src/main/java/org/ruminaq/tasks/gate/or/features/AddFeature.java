package org.ruminaq.tasks.gate.or.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.tasks.gate.features.AddGateFeature;
import org.ruminaq.tasks.gate.or.Images;

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
    return Images.K.IMG_OR_DIAGRAM.name();
  }
}
