/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.features.create.CreateTaskFeature;
import org.ruminaq.gui.features.create.PaletteCreateFeature;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.constant.impl.Port;
import org.ruminaq.tasks.constant.impl.strategy.Int32Strategy;
import org.ruminaq.tasks.constant.model.constant.Constant;
import org.ruminaq.tasks.constant.model.constant.ConstantFactory;

/**
 * Constant create feature.
 * 
 * @author Marek Jagielski
 */
public class CreateFeature extends CreateTaskFeature
    implements PaletteCreateFeature {

  public CreateFeature(IFeatureProvider fp) {
    super(fp, Constant.class);
  }

  @Override
  public String getCompartment() {
    return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
  }

  @Override
  public String getStack() {
    return CommonPaletteCompartmentEntry.SOURCES_STACK;
  }

  @Override
  public Object[] create(ICreateContext context) {
    Constant constant = ConstantFactory.eINSTANCE.createConstant();
    Object[] ret = super.create(context, constant);
    constant.setDataType(
        EcoreUtil.copy(constant.getOutputPort().get(0).getDataType().get(0)));
    constant.setValue(Int32Strategy.DEFAULT_VALUE);
//      UpdateContext updateCtx = new UpdateContext(Graphiti.getLinkService()
//          .getPictogramElements(getDiagram(), (Constant) os[0]).get(0));
//      getFeatureProvider().updateIfPossible(updateCtx);
    return ret;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }

  @Override
  public String getCreateImageId() {
    return Images.IMG_CONSTANT_PALETTE;
  }
}