/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.ruminaq.gui.features.update.UpdateTaskFeature;
import org.ruminaq.tasks.constant.model.constant.Constant;

public class UpdateFeature extends UpdateTaskFeature {

  private boolean updateNeededChecked = false;

  private boolean superUpdateNeeded = false;
  private boolean fillingUpdateNeeded = false;

  public UpdateFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    return (bo instanceof Constant);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    this.updateNeededChecked = true;
    superUpdateNeeded = super.updateNeeded(context).toBoolean();

    Constant constant = (Constant) getBusinessObjectForPictogramElement(
        context.getPictogramElement());

    fillingUpdateNeeded = updateFillingNeeded(constant,
        (ContainerShape) context.getPictogramElement());

    boolean updateNeeded = superUpdateNeeded || fillingUpdateNeeded;
    return updateNeeded ? Reason.createTrueReason()
        : Reason.createFalseReason();
  }

  @Override
  public boolean update(IUpdateContext context) {
    if (!updateNeededChecked)
      if (!this.updateNeeded(context).toBoolean())
        return false;

    Constant constant = (Constant) getBusinessObjectForPictogramElement(
        context.getPictogramElement());

    boolean updated = false;
    if (superUpdateNeeded)
      updated = updated | super.update(context);
    if (fillingUpdateNeeded)
      updated = updated | updateFilling(constant,
          (ContainerShape) context.getPictogramElement());
    return updated;
  }

  private boolean updateFillingNeeded(Constant constant,
      ContainerShape pictogramElement) {
    GraphicsAlgorithm insideText = null;
    for (GraphicsAlgorithm ga : pictogramElement.getGraphicsAlgorithm()
        .getGraphicsAlgorithmChildren()) {
      if (ga instanceof MultiText)
        insideText = ga;
    }
    if (insideText == null)
      return false;

    if (((MultiText) insideText).getValue() == null)
      return true;
    else
      return !((MultiText) insideText).getValue().equals(constant.getValue());
  }

  private boolean updateFilling(Constant constant,
      ContainerShape pictogramElement) {
    IGaService gaService = Graphiti.getGaService();
    GraphicsAlgorithm insideText = null;
    RoundedRectangle rr = null;
    for (GraphicsAlgorithm ga : pictogramElement.getGraphicsAlgorithm()
        .getGraphicsAlgorithmChildren()) {
      if (ga instanceof MultiText)
        insideText = ga;
      if (ga instanceof RoundedRectangle)
        rr = (RoundedRectangle) ga;
    }
    if (insideText == null)
      return false;
    else
      pictogramElement.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()
          .remove(insideText);

    String insideString = constant.getValue();

    MultiText text = gaService.createDefaultMultiText(getDiagram(),
        pictogramElement.getGraphicsAlgorithm(), insideString);
    gaService.setLocationAndSize(text, 7, 7, rr.getWidth() - 14,
        rr.getHeight() - 14);
//    text.setStyle(TaskStyle.getStyle(getDiagram()));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);

    return true;
  }
}
