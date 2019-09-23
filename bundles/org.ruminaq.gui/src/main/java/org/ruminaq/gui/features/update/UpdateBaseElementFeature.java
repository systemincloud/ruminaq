/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.ruminaq.model.ruminaq.BaseElement;

public class UpdateBaseElementFeature extends AbstractUpdateFeature {

  public UpdateBaseElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    return (bo instanceof BaseElement)
        && (context.getPictogramElement() instanceof ContainerShape);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    return Reason.createFalseReason();
  }

  @Override
  public boolean update(IUpdateContext context) {
    return false;
  }

}
