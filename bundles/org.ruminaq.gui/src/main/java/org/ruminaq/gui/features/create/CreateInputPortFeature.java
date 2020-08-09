/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.create;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.image.Images;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.RuminaqFactory;

/**
 * InputPort create feature.
 *
 * @author Marek Jagielski
 */
public class CreateInputPortFeature extends AbstractCreateElementFeature
    implements PaletteCreateFeature {

  public CreateInputPortFeature(IFeatureProvider fp) {
    super(fp, InputPort.class);
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
    InputPort inputPort = RuminaqFactory.eINSTANCE.createInputPort();
    setDefaultId(inputPort, context);

    getRuminaqDiagram().getMainTask().getInputPort().add(inputPort);

    addGraphicalRepresentation(context, inputPort);
    return new Object[] { inputPort };
  }

  @Override
  public String getCreateImageId() {
    return Images.IMG_PALETTE_INPUTPORT;
  }
}
