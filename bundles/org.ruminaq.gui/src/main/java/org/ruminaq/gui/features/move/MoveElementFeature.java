/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.move;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.LabelUtil;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.move.MoveElementFeature.Filter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.model.ruminaq.BaseElement;

@FeatureFilter(Filter.class)
public class MoveElementFeature extends DefaultMoveShapeFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context, IFeatureProvider fp) {
      IMoveShapeContext moveShapeContext = (IMoveShapeContext) context;
      Shape shape = moveShapeContext.getShape();

      Object bo = fp.getBusinessObjectForPictogramElement(shape);

      return bo instanceof BaseElement;
    }
  }

  public MoveElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    if (context.getSourceContainer() == null)
      return false;
    if (context.getSourceContainer().equals(context.getTargetContainer()))
      return true;

    Shape shape = context.getShape();
    ContainerShape targetShape = null;

    // can move on label place
    for (EObject o : shape.getLink().getBusinessObjects())
      if (LabelShape.class.isInstance(o))
        targetShape = (ContainerShape) o;
    if (targetShape != null && targetShape.equals(context.getTargetContainer()))
      return true;

    return false;
  }

  @Override
  protected void preMoveShape(IMoveShapeContext context) {
    super.preMoveShape(context);
  }

  @Override
  public void moveShape(IMoveShapeContext context) {
    Shape shape = context.getTargetContainer();
    if (LabelShape.class.isInstance(shape)) {
      MoveShapeContext c = (MoveShapeContext) context;
      c.setTargetContainer(shape.getContainer());
      c.setDeltaX(c.getDeltaX() + shape.getGraphicsAlgorithm().getX());
      c.setDeltaY(c.getDeltaY() + shape.getGraphicsAlgorithm().getY());
      c.setX(c.getX() + shape.getGraphicsAlgorithm().getX());
      c.setY(c.getY() + shape.getGraphicsAlgorithm().getY());
    }
    super.moveShape(context);
  }

  @Override
  protected void postMoveShape(final IMoveShapeContext context) {
    Shape shape = context.getShape();

    // move also label
    for (EObject o : shape.getLink().getBusinessObjects()) {
      if (LabelShape.class.isInstance(o)) {
        ContainerShape textContainerShape = (ContainerShape) o;
        int dx = context.getDeltaX();
        int dy = context.getDeltaY();

        textContainerShape.getGraphicsAlgorithm()
            .setX(textContainerShape.getGraphicsAlgorithm().getX() + dx);
        textContainerShape.getGraphicsAlgorithm()
            .setY(textContainerShape.getGraphicsAlgorithm().getY() + dy);
      }
    }

    super.postMoveShape(context);
  }
}
