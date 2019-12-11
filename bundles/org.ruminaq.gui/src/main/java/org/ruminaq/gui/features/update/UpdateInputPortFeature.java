/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.ruminaq.model.ruminaq.InputPort;

public class UpdateInputPortFeature extends UpdateBaseElementFeature {

  private boolean superUpdateNeeded = false;

  public UpdateInputPortFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    return (bo instanceof InputPort);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    superUpdateNeeded = super.updateNeeded(context).toBoolean();

//    ContainerShape parent = (ContainerShape) context.getPictogramElement();
//    InputPort ip = (InputPort) getBusinessObjectForPictogramElement(parent);
//
//    boolean asynchronousEq = ip.isAsynchronous() != LineStyle.SOLID
//        .equals(parent.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()
//            .get(0).getLineStyle());
//
//    boolean updateNeeded = superUpdateNeeded || !asynchronousEq;
//    return updateNeeded ? Reason.createTrueReason()
//        : Reason.createFalseReason();
    return Reason.createFalseReason();
  }

  @Override
  public boolean update(IUpdateContext context) {
    if (superUpdateNeeded)
      return update(context);

    ContainerShape parent = (ContainerShape) context.getPictogramElement();
    InputPort ip = (InputPort) getBusinessObjectForPictogramElement(parent);

    // ASYNCHRONOUS
    if (ip.isAsynchronous())
      parent.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0)
          .setLineStyle(LineStyle.DOT);
    else
      parent.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0)
          .setLineStyle(LineStyle.SOLID);

    return true;
  }

}
