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
import org.ruminaq.tasks.mux.model.DemuxPort;
import org.ruminaq.tasks.mux.model.mux.Demux;
import org.ruminaq.tasks.mux.model.mux.MuxFactory;

/**
 * Demux create feature.
 */
public class CreateDemuxFeature extends AbstractCreateTaskFeature
    implements PaletteCreateFeature {

  public CreateDemuxFeature(IFeatureProvider fp) {
    super(fp, Demux.class);
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
    return super.create(context, MuxFactory.eINSTANCE.createDemux());
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return DemuxPort.class;
  }

  @Override
  public String getCreateImageId() {
    return Images.IMG_DEMUX_PALETTE;
  }
}