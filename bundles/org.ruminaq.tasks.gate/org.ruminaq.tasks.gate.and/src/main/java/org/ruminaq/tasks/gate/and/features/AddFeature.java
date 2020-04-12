package org.ruminaq.tasks.gate.and.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.tasks.gate.and.Images;
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
    return Images.Image.IMG_AND_DIAGRAM.name();
  }
}
