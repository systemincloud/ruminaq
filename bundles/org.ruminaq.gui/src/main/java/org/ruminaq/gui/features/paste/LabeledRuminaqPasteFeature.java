/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.impl.label.LabelUtil;

/**
 * IPasteFeature for Ruminaq shape with Label.
 *
 * @author Marek Jagielski
 *
 * @param <T> LabeledRuminaqShape
 */
public class LabeledRuminaqPasteFeature<T extends LabeledRuminaqShape>
    extends RuminaqShapePasteFeature<T> {

  protected LabelShape oldLabel;

  protected LabeledRuminaqPasteFeature(IFeatureProvider fp, T oldPe, int xMin,
      int yMin) {
    super(fp, oldPe, xMin, yMin);
    this.oldLabel = oldPe.getLabel();
  }

  @Override
  public void paste(IPasteContext context) {
    super.paste(context);
    LabelShape newLabel = EcoreUtil.copy(oldLabel);
    newLabel.setX(newLabel.getX() + context.getX() - xMin);
    newLabel.setY(newLabel.getY() + context.getY() - yMin);
    newLabel.setLabeledShape(newPe);
    newPe.setLabel(newLabel);
    newPes.add(newLabel);
    getDiagram().getChildren().add(newLabel);

    if (LabelUtil.isInDefaultPosition(oldLabel)) {
      LabelUtil.placeInDefaultPosition(newLabel);
    }

    updatePictogramElement(newLabel);
    layoutPictogramElement(newLabel);
  }
}
