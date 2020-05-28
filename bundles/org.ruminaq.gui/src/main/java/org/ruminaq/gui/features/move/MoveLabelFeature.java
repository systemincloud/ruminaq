/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.move;

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.move.MoveLabelFeature.Filter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.impl.label.LabelUtil;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.NoElement;

@FeatureFilter(Filter.class)
public class MoveLabelFeature extends DefaultMoveShapeFeature {

  public static class Filter extends AbstractMoveFeatureFilter {
    @Override
    public Class<? extends RuminaqShape> forShape() {
      return LabelShape.class;
    }

    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return NoElement.class;
    }
  }

  public MoveLabelFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    if (Optional.ofNullable(context.getSourceContainer()).isEmpty()) {
      return false;
    }
    if (context.getSourceContainer().equals(context.getTargetContainer())) {
      return true;
    }
    return false;
  }

  @Override
  protected void preMoveShape(IMoveShapeContext context) {
    super.preMoveShape(context);
  }

  @Override
  public void moveShape(IMoveShapeContext context) {
    Shape sh = context.getShape();
    PictogramElement[] selection = getFeatureProvider().getDiagramTypeProvider()
        .getDiagramBehavior().getDiagramContainer()
        .getSelectedPictogramElements();
    for (EObject eo : Graphiti.getLinkService()
        .getAllBusinessObjectsForLinkedPictogramElement(sh))
      for (PictogramElement s : selection)
        if (eo == s)
          return;
    super.moveShape(context);
  }

  @Override
  protected void postMoveShape(final IMoveShapeContext context) {
    LabelShape shape = Optional.ofNullable(context.getShape())
        .filter(LabelShape.class::isInstance).map(LabelShape.class::cast)
        .orElseThrow();
    shape.setDefaultPosition(LabelUtil.isInDefaultPosition(shape));
    super.postMoveShape(context);
  }
}
