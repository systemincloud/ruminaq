package org.ruminaq.tasks.console.gui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.AddFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddFeatureFilter;
import org.ruminaq.gui.features.add.AddTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.console.gui.AddFeatureImpl.AddFeature.Filter;
import org.ruminaq.tasks.console.impl.Port;
import org.ruminaq.tasks.console.model.console.Console;

@Component(property = { "service.ranking:Integer=10" })
public class AddFeatureImpl implements AddFeatureExtension {

  @Override
  public List<Class<? extends IAddFeature>> getFeatures() {
    return Arrays.asList(AddFeature.class);
  }

  @FeatureFilter(Filter.class)
  public static class AddFeature extends AddTaskFeature {

    public static class Filter extends AbstractAddFeatureFilter {
      @Override
      public Class<? extends BaseElement> forBusinessObject() {
        return Console.class;
      }
    }

    public AddFeature(IFeatureProvider fp) {
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
    protected boolean useIconInsideShape() {
      return true;
    }

    @Override
    protected String getInsideIconId() {
      return Images.IMG_CONSOLE_DIAGRAM;
    }

    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }
  }
}
