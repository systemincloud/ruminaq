/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.mux.gui;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.features.create.AbstractCreateTaskFeature;
import org.ruminaq.gui.features.create.PaletteCreateFeature;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.mux.model.MuxPort;
import org.ruminaq.tasks.mux.model.mux.Mux;
import org.ruminaq.tasks.mux.model.mux.MuxFactory;

public class CreateMuxFeature extends AbstractCreateTaskFeature
    implements PaletteCreateFeature {

  public CreateMuxFeature(IFeatureProvider fp) {
    super(fp, Mux.class);
  }

  @Override
  public String getCompartment() {
    return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
  }

  @Override
  public String getStack() {
    return CommonPaletteCompartmentEntry.FLOW_STACK;
  }

  @Override
  public Object[] create(ICreateContext context) {
    return super.create(context, MuxFactory.eINSTANCE.createMux());
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return MuxPort.class;
  }

  @Override
  public String getCreateImageId() {
    return Images.IMG_MUX_PALETTE;
  }
}