package org.ruminaq.gui.features.move;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.model.diagram.impl.GuiUtil;

public class MoveInternalPortFeature extends DefaultMoveShapeFeature {

  public static final int EPSILON = 10;

  public MoveInternalPortFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    boolean ret = true;

    ret = ret && context.getSourceContainer() != null;
    ret = ret
        && context.getSourceContainer().equals(context.getTargetContainer());
    ret = ret && isOnBound(context);

    return ret;
  }

  private boolean isOnBound(IMoveShapeContext context) {
    int x = context.getPictogramElement().getGraphicsAlgorithm().getX();
    int y = context.getPictogramElement().getGraphicsAlgorithm().getY();
    int W = context.getTargetContainer().getGraphicsAlgorithm().getWidth();
    int H = context.getTargetContainer().getGraphicsAlgorithm().getHeight();
    int dx = context.getDeltaX();
    int dy = context.getDeltaY();
    int w = context.getPictogramElement().getGraphicsAlgorithm().getWidth();
    int h = context.getPictogramElement().getGraphicsAlgorithm().getHeight();

    int newX = x + dx;
    int newY = y + dy;

    if (newY < 0 || newX < 0)
      return false;
    if (newY + h > H || newX + w > W)
      return false;

    if (newX < EPSILON || GuiUtil.almostEqualRight(W - w, newX, EPSILON)
        || newY < EPSILON
        || GuiUtil.almostEqualRight(H - h, newY, EPSILON))
      return true;
    else
      return false;
  }

  @Override
  public void postMoveShape(final IMoveShapeContext context) {
    super.postMoveShape(context);

    Shape shape = context.getShape();

    // move also label
    for (EObject o : shape.getLink().getBusinessObjects()) {
      if (o instanceof Shape && Graphiti.getPeService()
          .getPropertyValue((Shape) o, Constants.PORT_LABEL_PROPERTY) != null) {
        ContainerShape textContainerShape = (ContainerShape) o;
        MultiText text = (MultiText) textContainerShape.getGraphicsAlgorithm()
            .getGraphicsAlgorithmChildren().get(0);
        int x = context.getPictogramElement().getGraphicsAlgorithm().getX();
        int y = context.getPictogramElement().getGraphicsAlgorithm().getY();
        int W = context.getTargetContainer().getGraphicsAlgorithm().getWidth();
        int H = context.getTargetContainer().getGraphicsAlgorithm().getHeight();
        int w = context.getPictogramElement().getGraphicsAlgorithm().getWidth();
        int h = context.getPictogramElement().getGraphicsAlgorithm()
            .getHeight();

        if (x < EPSILON) {
          context.getPictogramElement().getGraphicsAlgorithm().setX(0);
          GuiUtil.onRightOfShape(text, textContainerShape, w, h, 0, y, 0,
              0);
        } else if (GuiUtil.almostEqualRight(W - w, x, EPSILON)) {
          context.getPictogramElement().getGraphicsAlgorithm().setX(W - w);
          GuiUtil.onLeftOfShape(text, textContainerShape, w, h, W - w, y,
              0, 0);
        } else if (y < EPSILON) {
          context.getPictogramElement().getGraphicsAlgorithm().setY(0);
          GuiUtil.onBottomOfShape(text, textContainerShape, w, h, x, 0, 0,
              0);
        } else if (GuiUtil.almostEqualRight(H - h, y, EPSILON)) {
          context.getPictogramElement().getGraphicsAlgorithm().setY(H - h);
          GuiUtil.onTopOfShape(text, textContainerShape, w, h, x, H - h, 0,
              0);
        }

      }
    }
  }
}
