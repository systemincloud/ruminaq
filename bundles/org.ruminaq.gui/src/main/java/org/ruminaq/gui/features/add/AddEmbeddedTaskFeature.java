package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.add.AddEmbeddedTaskFeature.Filter;
import org.ruminaq.gui.EmbeddedTaskPorts;
import org.ruminaq.gui.Images;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.EmbeddedTask;

@FeatureFilter(Filter.class)
public class AddEmbeddedTaskFeature extends AddTaskFeature {

  public static class Filter extends AbstractAddFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return EmbeddedTask.class;
    }
  }
  
  public static String NOT_CHOSEN = "???";

  public AddEmbeddedTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected boolean useIconInsideShape() {
    return true;
  }

  @Override
  protected String getInsideIconId() {
    return  Images.Image.IMG_EMBEDDEDTASK_DIAGRAM_MAIN.name();
  }

  @Override
  protected String getInsideIconDesc() {
    return NOT_CHOSEN;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return EmbeddedTaskPorts.class;
  }
}