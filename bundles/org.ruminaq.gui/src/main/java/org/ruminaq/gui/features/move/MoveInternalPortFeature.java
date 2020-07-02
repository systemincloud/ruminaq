/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.move;

import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.move.MoveInternalPortFeature.Filter;
import org.ruminaq.gui.model.GuiUtil;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.InternalPort;

@FeatureFilter(Filter.class)
public class MoveInternalPortFeature extends DefaultMoveShapeFeature {

  public static final int EPSILON = 10;

  public static class Filter extends AbstractMoveFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return InternalPort.class;
    }
  }

  protected static InternalPortShape shapeFromContext(
      IMoveShapeContext context) {
    return Optional.of(context).map(IMoveShapeContext::getPictogramElement)
        .filter(InternalPortShape.class::isInstance)
        .map(InternalPortShape.class::cast)
        .orElseThrow(() -> new RuntimeException());
  }

  public MoveInternalPortFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return context.getTargetContainer() == null && isOnBound(context);
  }

  private boolean isOnBound(IMoveShapeContext context) {
    InternalPortShape ips = shapeFromContext(context);
    int W = ips.getTask().getWidth();
    int H = ips.getTask().getHeight();
    int w = ips.getWidth();
    int h = ips.getHeight();

    int newX = ips.getX() + context.getDeltaX();
    int newY = ips.getY() + context.getDeltaY();

    if (newY < 0 || newX < 0 || newY + h > H || newX + w > W) {
      return false;
    }

    return newX < EPSILON || GuiUtil.almostEqualRight(W - w, newX, EPSILON)
        || newY < EPSILON || GuiUtil.almostEqualRight(H - h, newY, EPSILON);
  }

  @Override
  public void postMoveShape(IMoveShapeContext context) {
//    Shape shape = context.getShape();
//
//    for (EObject o : shape.getLink().getBusinessObjects()) {
//      if (o instanceof Shape && Graphiti.getPeService()
//          .getPropertyValue((Shape) o, Constants.PORT_LABEL_PROPERTY) != null) {
//        ContainerShape textContainerShape = (ContainerShape) o;
//        MultiText text = (MultiText) textContainerShape.getGraphicsAlgorithm()
//            .getGraphicsAlgorithmChildren().get(0);
//        int x = context.getPictogramElement().getGraphicsAlgorithm().getX();
//        int y = context.getPictogramElement().getGraphicsAlgorithm().getY();
//        int W = context.getTargetContainer().getGraphicsAlgorithm().getWidth();
//        int H = context.getTargetContainer().getGraphicsAlgorithm().getHeight();
//        int w = context.getPictogramElement().getGraphicsAlgorithm().getWidth();
//        int h = context.getPictogramElement().getGraphicsAlgorithm()
//            .getHeight();
//
//        if (x < EPSILON) {
//          context.getPictogramElement().getGraphicsAlgorithm().setX(0);
//          GuiUtil.onRightOfShape(text, textContainerShape, w, h, 0, y, 0, 0);
//        } else if (GuiUtil.almostEqualRight(W - w, x, EPSILON)) {
//          context.getPictogramElement().getGraphicsAlgorithm().setX(W - w);
//          GuiUtil.onLeftOfShape(text, textContainerShape, w, h, W - w, y, 0, 0);
//        } else if (y < EPSILON) {
//          context.getPictogramElement().getGraphicsAlgorithm().setY(0);
//          GuiUtil.onBottomOfShape(text, textContainerShape, w, h, x, 0, 0, 0);
//        } else if (GuiUtil.almostEqualRight(H - h, y, EPSILON)) {
//          context.getPictogramElement().getGraphicsAlgorithm().setY(H - h);
//          GuiUtil.onTopOfShape(text, textContainerShape, w, h, x, H - h, 0, 0);
//        }
//
//      }
//    }
  }
}
