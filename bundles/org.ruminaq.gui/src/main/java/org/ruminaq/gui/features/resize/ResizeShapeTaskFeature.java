/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import java.util.Optional;
import java.util.OptionalInt;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.move.MoveInternalPortFeature;
import org.ruminaq.gui.features.resize.ResizeShapeTaskFeature.Filter;
import org.ruminaq.gui.model.GuiUtil;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.gui.model.diagram.impl.label.LabelUtil;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.Task;

/**
 * IResizeShapeFeature for Task.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class ResizeShapeTaskFeature extends DefaultResizeShapeFeature {

  public static class Filter extends AbstractResizeFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return Task.class;
    }
  }

  public ResizeShapeTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected static TaskShape shapeFromContext(IResizeShapeContext context) {
    return Optional.of(context).map(IResizeShapeContext::getShape)
        .filter(TaskShape.class::isInstance).map(TaskShape.class::cast)
        .orElseThrow();
  }

  @Override
  public boolean canResizeShape(IResizeShapeContext context) {
    return true;
  }

  @Override
  public void resizeShape(IResizeShapeContext context) {
    TaskShape shape = shapeFromContext(context);
    final boolean labelInDefaultPostion = LabelUtil
        .isInDefaultPosition(shape.getLabel());

    final int widthBefore = shape.getWidth();
    final int heightBefore = shape.getHeight();

    int x = context.getX();
    int y = context.getY();
    int width = context.getWidth();
    int height = context.getHeight();

    shape.setX(x);
    shape.setY(y);
    shape.setWidth(width);
    shape.setHeight(height);

    alignInternalPorts(shape, widthBefore, heightBefore, width, height);

    if (labelInDefaultPostion || GuiUtil.intersects(shape.getLabel(), shape)) {
      LabelUtil.placeInDefaultPosition(shape.getLabel());
    }

    layoutPictogramElement(shape);
  }

  private int outOfBorder(int delta, int xy, int shapeSize, int sizeAfter) {
    return OptionalInt.of(delta).stream()
        .filter(i -> xy + i + shapeSize > sizeAfter)
        .map(i -> sizeAfter - xy - shapeSize).findAny().orElse(delta);
  }

  private int almostOnBorder(int delta, int xy, int shapeSize, int sizeAfter) {
    return OptionalInt.of(delta).stream().filter(
        i -> xy + i + shapeSize < sizeAfter - MoveInternalPortFeature.EPSILON)
        .map(i -> sizeAfter - xy - shapeSize).findAny().orElse(delta);
  }

  private void alignInternalPorts(TaskShape shape, int widthBefore,
      int heightBefore, int widthAfter, int heightAfter) {

    float wRatio = (float) widthAfter / (float) widthBefore;
    float hRatio = (float) heightAfter / (float) heightBefore;

    for (InternalPortShape child : shape.getInternalPort()) {
      int xPort = child.getX();
      int yPort = child.getY();
      int dx = Math.round(xPort * wRatio) - xPort;
      int dy = Math.round(yPort * hRatio) - yPort;
      if (GuiUtil.isOnBorder(shape, widthBefore, heightBefore)) {
        dx = outOfBorder(dx, xPort, shape.getWidth(), widthAfter);
        dx = almostOnBorder(dx, xPort, shape.getWidth(), widthAfter);
        dy = outOfBorder(dy, yPort, child.getHeight(), heightAfter);
        dy = almostOnBorder(dy, yPort, child.getHeight(), heightAfter);
      }
      MoveShapeContext moveShapeContext = new MoveShapeContext(child);
      moveShapeContext.setX(xPort + dx);
      moveShapeContext.setY(yPort + dy);
      moveShapeContext.setDeltaX(dx);
      moveShapeContext.setDeltaY(dy);
      MoveInternalPortFeature moveFeature = new MoveInternalPortFeature(
          getFeatureProvider());
      if (moveFeature.canMoveShape(moveShapeContext)) {
        moveFeature.moveShape(moveShapeContext);
        moveFeature.postMoveShape(moveShapeContext);
      }
    }
  }
}
