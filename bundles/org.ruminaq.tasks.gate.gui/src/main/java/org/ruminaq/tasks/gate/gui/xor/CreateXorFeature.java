/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.gate.gui.xor;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.features.create.AbstractCreateTaskFeature;
import org.ruminaq.gui.features.create.PaletteCreateFeature;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.gate.gui.Images;
import org.ruminaq.tasks.gate.gui.Port;
import org.ruminaq.tasks.gate.model.gate.GateFactory;
import org.ruminaq.tasks.gate.model.gate.Xor;

public class CreateXorFeature extends AbstractCreateTaskFeature
    implements PaletteCreateFeature {

  public CreateXorFeature(IFeatureProvider fp) {
    super(fp, Xor.class);
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
    return super.create(context, GateFactory.eINSTANCE.createXor());
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }

  @Override
  public String getCreateImageId() {
    return Images.IMG_XOR_PALETTE;
  }
}
