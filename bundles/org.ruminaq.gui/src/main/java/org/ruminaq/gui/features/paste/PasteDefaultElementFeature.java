/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.GuiUtil;
import org.ruminaq.gui.LabelUtil;
import org.ruminaq.gui.features.create.AbstractCreateElementFeature;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.impl.factories.LabelShapeFactory;
import org.ruminaq.model.ruminaq.BaseElement;

public class PasteDefaultElementFeature extends RuminaqPasteFeature {

  public PasteDefaultElementFeature(IFeatureProvider fp, PictogramElement oldPe,
      int xMin, int yMin) {
    super(fp);
  }

  @Override
  public List<PictogramElement> getNewPictogramElements() {
    return newPes;
  }

  @Override
  public boolean canPaste(IPasteContext context) {
    PictogramElement[] pes = context.getPictogramElements();
    if (pes.length != 1 || !(pes[0] instanceof Diagram))
      return false;
    return false;
  }

  @Override
  public void paste(IPasteContext context) {
  }

  public static String setId(String baseId, BaseElement element,
      Diagram diagram) {
    String name = baseId;
    if (AbstractCreateElementFeature.isPresent(name, diagram.getChildren())) {
      name = "(Copy) " + baseId;
      if (AbstractCreateElementFeature.isPresent(name, diagram.getChildren())) {
        int i = 1;
        while (AbstractCreateElementFeature.isPresent(name + " " + i,
            diagram.getChildren()))
          i++;
        name = name + " " + i;
      }
    }

    element.setId(name);
    return name;
  }

  public static ContainerShape addLabel(PictogramElement oldPe,
      LabelShape oldLabel, int x, int y, String newId, Diagram diagram,
      PictogramElement newPe) {
    boolean labelInDefaultPosition = LabelUtil
        .isLabelInDefaultPosition(oldLabel, oldPe);
    LabelShape newLabel = EcoreUtil.copy(oldLabel);
    newLabel.getGraphicsAlgorithm().setX(newLabel.getGraphicsAlgorithm().getX()
        + x - oldPe.getGraphicsAlgorithm().getX());
    newLabel.getGraphicsAlgorithm().setY(newLabel.getGraphicsAlgorithm().getY()
        + y - oldPe.getGraphicsAlgorithm().getY());
    ((MultiText) newLabel.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()
        .get(0)).setValue(newId);
    diagram.getChildren().add(newLabel);
    if (labelInDefaultPosition) {
      LabelShapeFactory.placeLabelInDefaultPosition(newLabel);
    }
    return newLabel;
  }
}
