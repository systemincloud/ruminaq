/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.inspect.gui;

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
import org.ruminaq.model.ruminaq.PortsDescr;
import org.ruminaq.tasks.inspect.model.Port;
import org.ruminaq.tasks.inspect.model.inspect.Inspect;
import org.ruminaq.tasks.inspect.model.inspect.InspectFactory;

/**
 * Service CreateFeaturesExtension implementation.
 * 
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=10" })
public class CreateFeaturesImpl implements CreateFeaturesExtension {

  @Override
  public List<Class<? extends ICreateFeature>> getFeatures() {
    return Arrays.asList(CreateFeature.class);
  }

  public static class CreateFeature extends AbstractCreateTaskFeature
      implements PaletteCreateFeature {

    public CreateFeature(IFeatureProvider fp) {
      super(fp, Inspect.class);
    }

    @Override
    public String getCompartment() {
      return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
    }

    @Override
    public String getStack() {
      return CommonPaletteCompartmentEntry.SINKS_STACK;
    }

    @Override
    public Object[] create(ICreateContext context) {
      Inspect inspect = InspectFactory.eINSTANCE.createInspect();
      return super.create(context, inspect);
    }

    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }

    @Override
    public String getCreateImageId() {
      return Images.IMG_INSPECT_PALETTE;
    }
  }
}
