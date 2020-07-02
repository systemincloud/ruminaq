/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.randomgenerator.gui.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;
import org.ruminaq.gui.features.update.UpdateTaskFeature;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;

public class UpdateFeature extends UpdateTaskFeature {

  private boolean updateNeededChecked = false;

  private boolean superUpdateNeeded = false;
  private boolean descUpdateNeeded = false;

  public UpdateFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    return (bo instanceof RandomGenerator);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    this.updateNeededChecked = true;
    superUpdateNeeded = super.updateNeeded(context).toBoolean();

    boolean updateNeeded = superUpdateNeeded || descUpdateNeeded;
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
    if (descUpdateNeeded)
      updated = updated | descUpdate(context.getPictogramElement());
    return updated;
  }

  private boolean descUpdate(PictogramElement pe) {
//    Object bo = getBusinessObjectForPictogramElement(pe);
//    if (bo instanceof RandomGenerator) {
//      DataType dt = ((RandomGenerator) bo).getDataType();
//      if (dt != null) {
//        String dataType = ModelUtil.getName(dt.getClass(), false);
//        for (GraphicsAlgorithm ga : pe.getGraphicsAlgorithm()
//            .getGraphicsAlgorithmChildren())
//          if (Graphiti.getPeService().getProperty(ga,
//              AbstractAddTaskFeature.ICON_DESC_PROPERTY) != null) {
//            ((Text) ga).setValue(dataType);
//            return true;
//          }
//      }
//    }
    return false;
  }
}
