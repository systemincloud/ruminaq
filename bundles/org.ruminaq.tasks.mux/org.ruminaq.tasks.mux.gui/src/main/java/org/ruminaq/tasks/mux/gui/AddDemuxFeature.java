package org.ruminaq.tasks.mux.gui;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddFeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.mux.gui.AddDemuxFeature.Filter;
import org.ruminaq.tasks.mux.model.DemuxPort;
import org.ruminaq.tasks.mux.model.mux.Demux;

/**
 * Demux AddFeature.
 */
@FeatureFilter(Filter.class)
public class AddDemuxFeature extends AbstractAddTaskFeature {

  public static class Filter extends AbstractAddFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return Demux.class;
    }
  }

  public AddDemuxFeature(IFeatureProvider fp) {
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
  protected String getInsideIconId() {
    return Images.IMG_DEMUX_DIAGRAM;
  }
}