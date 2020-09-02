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
import org.ruminaq.model.desc.NoPorts;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.RuminaqFactory;

/**
 * EmbeddedTask create feature.
 *
 * @author Marek Jagielski
 */
public class CreateEmbeddedTaskFeature extends AbstractCreateTaskFeature
    implements PaletteCreateFeature {

  public CreateEmbeddedTaskFeature(IFeatureProvider fp) {
    super(fp, EmbeddedTask.class);
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
    return super.create(context, RuminaqFactory.eINSTANCE.createEmbeddedTask());
  }

  @Override
  public String getCreateImageId() {
    return Images.IMG_EMBEDDEDTASK_PALETTE;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return NoPorts.class;
  }
}
