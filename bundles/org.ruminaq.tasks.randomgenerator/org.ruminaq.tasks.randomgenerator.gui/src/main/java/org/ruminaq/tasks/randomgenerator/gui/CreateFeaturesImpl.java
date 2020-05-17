package org.ruminaq.tasks.randomgenerator.gui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CreateFeaturesExtension;
import org.ruminaq.gui.features.create.AbstractCreateTaskFeature;
import org.ruminaq.gui.features.create.PaletteCreateFeature;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.randomgenerator.impl.Port;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomgeneratorFactory;

@Component(property = { "service.ranking:Integer=10" })
public class CreateFeaturesImpl implements CreateFeaturesExtension {

  @Override
  public List<Class<? extends ICreateFeature>> getFeatures() {
    return Arrays.asList(CreateFeature.class);
  }

  public static class CreateFeature extends AbstractCreateTaskFeature
      implements PaletteCreateFeature {

    public CreateFeature(IFeatureProvider fp) {
      super(fp, RandomGenerator.class);
    }

    @Override
    public String getCompartment() {
      return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
    }

    @Override
    public String getStack() {
      return CommonPaletteCompartmentEntry.SOURCES_STACK;
    }

    @Override
    public Object[] create(ICreateContext context) {
      Object[] os = super.create(context,
          RandomgeneratorFactory.eINSTANCE.createRandomGenerator());
      ((RandomGenerator) os[0]).setDataType(EcoreUtil
          .copy(((Task) os[0]).getOutputPort().get(0).getDataType().get(0)));
      ((RandomGenerator) os[0]).setDimensions("1");

      return os;
    }

    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }

    @Override
    public String getCreateImageId() {
      return Images.IMG_RANDOMGENERATOR_PALETTE;
    }
  }
}
