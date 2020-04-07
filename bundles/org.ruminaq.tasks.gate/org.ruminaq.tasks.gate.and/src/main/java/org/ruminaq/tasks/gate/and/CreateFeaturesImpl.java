package org.ruminaq.tasks.gate.and;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CreateFeaturesExtension;
import org.ruminaq.gui.features.create.CreateTaskFeature;
import org.ruminaq.gui.features.create.PaletteCreateFeature;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.gate.Port;
import org.ruminaq.tasks.gate.and.model.and.And;
import org.ruminaq.tasks.gate.and.model.and.AndFactory;

@Component(property = { "service.ranking:Integer=10" })
public class CreateFeaturesImpl implements CreateFeaturesExtension {

  @Override
  public List<Class<? extends ICreateFeature>> getFeatures() {
    return Arrays.asList(CreateFeature.class);
  }

  public static class CreateFeature extends CreateTaskFeature
      implements PaletteCreateFeature {

    public CreateFeature(IFeatureProvider fp) {
      super(fp, And.class);
    }

    @Override
    public String getCompartment() {
      return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
    }

    @Override
    public String getStack() {
      return CommonPaletteCompartmentEntry.LOGIC_STACK;
    }

    @Override
    public Object[] create(ICreateContext context) {
      return super.create(context, AndFactory.eINSTANCE.createAnd());
    }

    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }

    @Override
    public String getCreateImageId() {
      return Images.K.IMG_AND_PALETTE.name();
    }
  }
}
