package org.ruminaq.gui.features.paste;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.impl.label.LabelUtil;

public abstract class LabeledRuminaqPasteFeature<T extends LabeledRuminaqShape>
    extends RuminaqPasteFeature<T> {

  protected LabelShape oldLabel;

  protected LabeledRuminaqPasteFeature(IFeatureProvider fp, T oldPe, int xMin,
      int yMin) {
    super(fp, oldPe, xMin, yMin);
    this.oldLabel = oldPe.getLabel();
  }

  @Override
  public T paste(int x, int y) {
    T newPe = super.paste(x, y);
    LabelShape newLabel = EcoreUtil.copy(oldLabel);
    newLabel.setX(newLabel.getX() + x - oldPe.getX());
    newLabel.setY(newLabel.getY() + y - oldPe.getY());
    getDiagram().getChildren().add(newLabel);
    if (LabelUtil.isInDefaultPosition(oldLabel)) {
      LabelUtil.placeInDefaultPosition(newLabel);
    }

    newPes.add(newLabel);
    newPe.setLabel(newLabel);

    updatePictogramElement(newLabel);
    layoutPictogramElement(newLabel);

    return newPe;
  }

}
