/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.create;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.Images;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.ModelHandler;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.model.ruminaq.RuminaqFactory;

public class CreateOutputPortFeature extends CreateElementFeature
    implements PaletteCreateFeature {

  public CreateOutputPortFeature(IFeatureProvider fp) {
    super(fp, OutputPort.class);
  }

  @Override
  public String getCompartment() {
    return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
  }

  @Override
  public String getStack() {
    return CommonPaletteCompartmentEntry.PORTS_STACK;
  }

  @Override
  public Object[] create(ICreateContext context) {
    OutputPort outputPort = RuminaqFactory.eINSTANCE.createOutputPort();
    setDefaultId(outputPort, context);

    MainTask mt = ModelHandler.getModel(getDiagram(), getFeatureProvider());
    mt.getOutputPort().add(outputPort);

    addGraphicalRepresentation(context, outputPort);
    return new Object[] { outputPort };
  }

  @Override
  public String getCreateImageId() {
    return Images.Image.IMG_PALETTE_OUTPUTPORT.name();
  }
}
