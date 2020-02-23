/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.ruminaq.gui.features.create.AbstractCreateElementFeature;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;

public abstract class RuminaqPasteFeature<T extends RuminaqShape>
    extends AbstractPasteFeature {

  protected List<PictogramElement> newPes = new LinkedList<>();

  protected T oldPe;

  protected int xMin;
  protected int yMin;

  public RuminaqPasteFeature(IFeatureProvider fp, T oldPe, int xMin,
      int yMin) {
    super(fp);
    this.oldPe = oldPe;
    this.xMin = xMin;
    this.yMin = yMin;
  }

  private static String getId(String baseId, Diagram diagram) {
    String name = baseId;
    if (AbstractCreateElementFeature.isPresent(name, diagram.getChildren())) {
      name = "(Copy) " + baseId;
      if (AbstractCreateElementFeature.isPresent(name, diagram.getChildren())) {
        int i = 1;
        while (AbstractCreateElementFeature.isPresent(name + " " + i,
            diagram.getChildren())) {
          i++;
        }
        name = name + " " + i;
      }
    }

    return name;
  }

  public List<PictogramElement> getNewPictogramElements() {
    return newPes;
  }

  protected RuminaqDiagram getRuminaqDiagram() {
    return (RuminaqDiagram) getDiagram();
  }

  public T paste(int x, int y) {
    T newPe = EcoreUtil.copy(oldPe);
    newPes.add(newPe);
    BaseElement newBo = EcoreUtil.copy(oldPe.getModelObject());
    newPe.setModelObject(newBo);
    newPe.setX(x + newPe.getX() - xMin);
    newPe.setY(y + newPe.getY() - yMin);
    newBo.setId(getId(newBo.getId(), getDiagram()));
    getDiagram().getChildren().add(newPe);

    return newPe;
  }
}
