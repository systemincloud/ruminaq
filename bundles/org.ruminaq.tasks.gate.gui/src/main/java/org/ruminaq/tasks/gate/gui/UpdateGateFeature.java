/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.gate.gui;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.ruminaq.gui.features.update.UpdateTaskFeature;
import org.ruminaq.tasks.gate.model.gate.Gate;

public class UpdateGateFeature extends UpdateTaskFeature {

  private boolean updateNeededChecked = false;

  private boolean superUpdateNeeded = false;
  private boolean inputsUpdateNeeded = false;

  public UpdateGateFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    return (bo instanceof Gate);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    this.updateNeededChecked = true;
    superUpdateNeeded = super.updateNeeded(context).toBoolean();

    ContainerShape parent = (ContainerShape) context.getPictogramElement();
    Gate gt = (Gate) getBusinessObjectForPictogramElement(parent);

    this.inputsUpdateNeeded = gt.getInputNumber() != gt.getInputPort().size();

    boolean updateNeeded = superUpdateNeeded || inputsUpdateNeeded;
    return updateNeeded ? Reason.createTrueReason()
        : Reason.createFalseReason();
  }

  @Override
  public boolean update(IUpdateContext context) {
    if (!updateNeededChecked)
      if (!this.updateNeeded(context).toBoolean())
        return false;

    boolean updated = false;
    if (superUpdateNeeded)
      updated = updated | super.update(context);

    ContainerShape parent = (ContainerShape) context.getPictogramElement();
    Gate gt = (Gate) getBusinessObjectForPictogramElement(parent);

    if (inputsUpdateNeeded)
      updated = updated | inputsUpdate(parent, gt);

    return updated;
  }

  private boolean inputsUpdate(ContainerShape parent, Gate gt) {
    int n = gt.getInputNumber() - gt.getInputPort().size();
    if (n > 0)
      for (int i = 0; i < n; i++)
        addPort(gt, parent, Port.IN);
    else if (n < 0)
      for (int i = 0; i < -n; i++)
        removePort(gt, parent, Port.IN);
    return true;
  }
}
