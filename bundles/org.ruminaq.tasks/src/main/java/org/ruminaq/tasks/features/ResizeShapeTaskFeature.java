package org.ruminaq.tasks.features;

import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.GuiUtil;
import org.ruminaq.gui.LabelUtil;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.impl.label.LabelShapeFactory;

public class ResizeShapeTaskFeature extends DefaultResizeShapeFeature {

  public ResizeShapeTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canResizeShape(IResizeShapeContext context) {
    return true;
  }

  @Override
  public void resizeShape(IResizeShapeContext context) {
    Shape shape = context.getShape();

    int w_before = shape.getGraphicsAlgorithm().getWidth();
    int h_before = shape.getGraphicsAlgorithm().getHeight();

    int x = context.getX();
    int y = context.getY();
    int width = context.getWidth();
    int height = context.getHeight();

    if (shape.getGraphicsAlgorithm() != null) {
      Graphiti.getGaService().setLocationAndSize(shape.getGraphicsAlgorithm(),
          x, y, width, height);
    }

    if (shape.getGraphicsAlgorithm() != null) {
      for (GraphicsAlgorithm ga : shape.getGraphicsAlgorithm()
          .getGraphicsAlgorithmChildren()) {
        if (Graphiti.getPeService().getProperty(ga,
            AddTaskFeature.ICON_DESC_PROPERTY) != null)
          Graphiti.getGaService().setLocationAndSize(ga, 0,
              AddTaskFeature.ICON_SIZE, width,
              height - AddTaskFeature.ICON_SIZE);
        else
          Graphiti.getGaService().setLocationAndSize(ga, 0, 0, width, height);
      }
    }

    alignInternalPorts(shape, w_before, h_before, width, height);

    Optional<LabeledRuminaqShape> labeledShape = Optional.of(shape)
        .filter(LabeledRuminaqShape.class::isInstance)
        .map(LabeledRuminaqShape.class::cast);
    
    if (labeledShape.isPresent()) {
      if (LabelUtil.isLabelInDefaultPosition(labeledShape.get().getLabel(),
          labeledShape.get())
          || isConflictingWithNewSize(labeledShape.get().getLabel(),
              labeledShape.get())) {
        LabelShapeFactory
            .placeLabelInDefaultPosition(labeledShape.get().getLabel());
      }
    }

    layoutPictogramElement(shape);
  }

  private boolean isConflictingWithNewSize(ContainerShape textContainerShape,
      Shape shape) {
    return GuiUtil.intersectsLabel(textContainerShape, shape);
  }

  private void alignInternalPorts(Shape shape, int w_before, int h_before,
      int w, int h) {

    float w_ratio = (float) w / (float) w_before;
    float h_ratio = (float) h / (float) h_before;

    for (Shape child : ((ContainerShape) shape).getChildren()) {
      String isInternalPort = Graphiti.getPeService().getPropertyValue(child,
          Constants.INTERNAL_PORT);
      if (Boolean.parseBoolean(isInternalPort)) {
        int dx = 0;
        int dy = 0;
        int xPort = child.getGraphicsAlgorithm().getX();
        int yPort = child.getGraphicsAlgorithm().getY();
        if (xPort == 0
            || xPort == (w_before - child.getGraphicsAlgorithm().getWidth())
            || yPort == 0
            || yPort == (h_before - child.getGraphicsAlgorithm().getHeight())) {
          dy = Math.round(yPort * h_ratio) - yPort;
          if (yPort + dy + child.getGraphicsAlgorithm().getHeight() > h)
            dy = h - yPort - child.getGraphicsAlgorithm().getHeight();
          if (yPort + dy + child.getGraphicsAlgorithm().getHeight() < h
              - MoveInternalPortFeature.EPSILON
              && yPort == (h_before - child.getGraphicsAlgorithm().getHeight()))
            dy = h - yPort - child.getGraphicsAlgorithm().getHeight();
          dx = Math.round(xPort * w_ratio) - xPort;
          if (xPort + dx + child.getGraphicsAlgorithm().getWidth() > w)
            dx = w - xPort - child.getGraphicsAlgorithm().getWidth();
          if (xPort + dx + child.getGraphicsAlgorithm().getWidth() < w
              - MoveInternalPortFeature.EPSILON
              && xPort == (w_before - child.getGraphicsAlgorithm().getWidth()))
            dx = w - xPort - child.getGraphicsAlgorithm().getWidth();
        }

        MoveShapeContext moveShapeContext = new MoveShapeContext(child);
        moveShapeContext.setX(xPort + dx);
        moveShapeContext.setY(yPort + dy);
        moveShapeContext.setDeltaX(dx);
        moveShapeContext.setDeltaY(dy);
        moveShapeContext.setSourceContainer((ContainerShape) shape);
        moveShapeContext.setTargetContainer((ContainerShape) shape);
        MoveInternalPortFeature moveFeature = new MoveInternalPortFeature(
            getFeatureProvider());
        if (moveFeature.canMoveShape(moveShapeContext)) {
          moveFeature.moveShape(moveShapeContext);
          moveFeature.postMoveShape(moveShapeContext);
        }
      }
    }
  }
}
