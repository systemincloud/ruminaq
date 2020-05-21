/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.gate.gui.not;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.features.create.AbstractCreateTaskFeature;
import org.ruminaq.gui.features.create.PaletteCreateFeature;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.gate.gui.Images;
import org.ruminaq.tasks.gate.model.gate.GateFactory;
import org.ruminaq.tasks.gate.model.gate.Not;
import org.ruminaq.tasks.gate.not.impl.Port;

public class CreateNotFeature extends AbstractCreateTaskFeature
    implements PaletteCreateFeature {

  public CreateNotFeature(IFeatureProvider fp) {
    super(fp, Not.class);
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
    Not gt = GateFactory.eINSTANCE.createNot();
    gt.setInputNumber(1);
    return super.create(context, gt);
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }

  @Override
  public String getCreateImageId() {
    return Images.IMG_NOT_PALETTE;
  }
}