/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import java.util.Optional;
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

  protected static TaskShape shapeFromContext(IResizeShapeContext context) {
    return Optional.of(context).map(IResizeShapeContext::getShape)
        .filter(TaskShape.class::isInstance).map(TaskShape.class::cast)
        .orElseThrow();
  }

  public ResizeShapeTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canResizeShape(IResizeShapeContext context) {
    return true;
  }

  @Override
  public void resizeShape(IResizeShapeContext context) {
    TaskShape shape = shapeFromContext(context);
    boolean labelInDefaultPostion = LabelUtil
        .isInDefaultPosition(shape.getLabel());

    int widthBefore = shape.getWidth();
    int heightBefore = shape.getHeight();

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

  private void alignInternalPorts(TaskShape shape, int widthBefore,
      int heightBefore, int w, int h) {

    float w_ratio = (float) w / (float) widthBefore;
    float h_ratio = (float) h / (float) heightBefore;

    for (InternalPortShape child : shape.getInternalPort()) {
      int dx = 0;
      int dy = 0;
      int xPort = child.getX();
      int yPort = child.getY();
      if (xPort == 0 || xPort == (widthBefore - child.getWidth()) || yPort == 0
          || yPort == (heightBefore - child.getHeight())) {
        dy = Math.round(yPort * h_ratio) - yPort;
        if (yPort + dy + child.getHeight() > h)
          dy = h - yPort - child.getHeight();
        if (yPort + dy + child.getHeight() < h - MoveInternalPortFeature.EPSILON
            && yPort == (heightBefore - child.getHeight()))
          dy = h - yPort - child.getHeight();
        dx = Math.round(xPort * w_ratio) - xPort;
        if (xPort + dx + child.getWidth() > w)
          dx = w - xPort - child.getWidth();
        if (xPort + dx + child.getWidth() < w - MoveInternalPortFeature.EPSILON
            && xPort == (widthBefore - child.getWidth()))
          dx = w - xPort - child.getWidth();
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
