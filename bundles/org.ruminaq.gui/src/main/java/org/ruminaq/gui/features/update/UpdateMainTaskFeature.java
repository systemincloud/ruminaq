/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.util.EclipseUtil;

public class UpdateMainTaskFeature extends AbstractUpdateFeature {

  public UpdateMainTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    if (updateNeeded(context).toBoolean())
      update(context);
    return false;
  }

  @Override
  public boolean update(IUpdateContext context) {
    String path = EclipseUtil.getModelPathFromEObject(getDiagram()).path();
    String tmp = path.substring(0, path.lastIndexOf("."));
    final String fileName = tmp.substring(tmp.lastIndexOf("/") + 1);

    TransactionalEditingDomain editingDomain = getDiagramBehavior()
        .getEditingDomain();

    ModelUtil.runModelChange(new Runnable() {
      @Override
      public void run() {
        getDiagram().setName(fileName);
      }
    }, editingDomain, "Update diagram name");

    return true;
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    Diagram d = (Diagram) context.getPictogramElement();
    String path = EclipseUtil.getModelPathFromEObject(d).path();
    String fileName = path.substring(0, path.lastIndexOf("."));
    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
    boolean updateNeeded = getDiagram().getName().equals(fileName) ? false
        : true;

    if (updateNeeded)
      return Reason.createTrueReason();
    else
      return Reason.createFalseReason();
  }
}
