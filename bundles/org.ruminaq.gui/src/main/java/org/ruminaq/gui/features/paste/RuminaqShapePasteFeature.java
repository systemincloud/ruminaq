/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.stream.Stream;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ruminaq.gui.features.create.AbstractCreateElementFeature;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * IPasteFeature for Ruminaq shape.
 *
 * @author Marek Jagielski
 *
 * @param <T> RuminaqShape
 */
public class RuminaqShapePasteFeature<T extends RuminaqShape>
    extends PictogramElementPasteFeature<T> {

  protected int xMin;
  protected int yMin;

  protected RuminaqShapePasteFeature(IFeatureProvider fp, T oldPe, int xMin,
      int yMin) {
    super(fp, oldPe);
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

  @Override
  public boolean canPaste(IPasteContext context) {
    return Stream.of(context.getPictogramElements()).findFirst()
        .filter(RuminaqDiagram.class::isInstance).isPresent();
  }

  @Override
  public void paste(IPasteContext context) {
    super.paste(context);
    BaseElement newBo = EcoreUtil.copy(oldPe.getModelObject());
    newPe.setModelObject(newBo);
    newPe.setX(context.getX() + newPe.getX() - xMin);
    newPe.setY(context.getY() + newPe.getY() - yMin);
    newBo.setId(getId(newBo.getId(), getDiagram()));
    getDiagram().getChildren().add(newPe);
  }
}
