package org.ruminaq.tasks.pythontask;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CreateFeaturesExtension;
import org.ruminaq.gui.features.create.AbstractCreateTaskFeature;
import org.ruminaq.gui.features.create.PaletteCreateFeature;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.pythontask.model.pythontask.PythonTask;
import org.ruminaq.tasks.pythontask.model.pythontask.PythontaskFactory;

@Component(property = { "service.ranking:Integer=10" })
public class CreateFeaturesImpl implements CreateFeaturesExtension {

  @Override
  public List<Class<? extends ICreateFeature>> getFeatures() {
    return Arrays.asList(CreateFeature.class);
  }

  public static class CreateFeature extends AbstractCreateTaskFeature
      implements PaletteCreateFeature {

    public CreateFeature(IFeatureProvider fp) {
      super(fp, PythonTask.class);
    }

    @Override
    public String getCompartment() {
      return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
    }

    @Override
    public String getStack() {
      return CommonPaletteCompartmentEntry.USERDEFINED_STACK;
    }

    @Override
    public Object[] create(ICreateContext context) {
      return super.create(context,
          PythontaskFactory.eINSTANCE.createPythonTask());
    }

    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }

    @Override
    public String getCreateImageId() {
      return Images.IMG_PYTHONTASK_PALETTE;
    }
  }
}
