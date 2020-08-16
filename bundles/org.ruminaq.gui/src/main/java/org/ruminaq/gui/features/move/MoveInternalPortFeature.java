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
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.InternalPort;

/**
 * IMoveShapeFeature for Task's internalport.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class MoveInternalPortFeature extends DefaultMoveShapeFeature {

  public static final int EPSILON = 10;

  public static class Filter extends AbstractMoveFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return InternalPort.class;
    }
  }

  public MoveInternalPortFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected static InternalPortShape shapeFromContext(
      IMoveShapeContext context) {
    return Optional.of(context).map(IMoveShapeContext::getPictogramElement)
        .filter(InternalPortShape.class::isInstance)
        .map(InternalPortShape.class::cast).orElseThrow(RuntimeException::new);
  }

  private static boolean isOnBound(IMoveShapeContext context) {
    InternalPortShape ips = shapeFromContext(context);
    TaskShape task = ips.getTask();

    int newX = ips.getX() + context.getDeltaX();
    int newY = ips.getY() + context.getDeltaY();

    if (newY < 0 || newX < 0 || newY + ips.getHeight() > task.getHeight()
        || newX + ips.getWidth() > task.getWidth()) {
      return false;
    }

    return newX < EPSILON || newY < EPSILON
        || GuiUtil.almostEqualRight(task.getWidth() - ips.getWidth(), newX,
            EPSILON)
        || GuiUtil.almostEqualRight(task.getHeight() - ips.getHeight(), newY,
            EPSILON);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return context.getTargetContainer() == null && isOnBound(context);
  }

  @Override
  public void postMoveShape(IMoveShapeContext context) {
    InternalPortShape ips = shapeFromContext(context);

    if (ips.getX() < EPSILON) {
      ips.setX(0);
    }

    if (ips.getY() < EPSILON) {
      ips.setY(0);
    }

    int taskWidth = ips.getTask().getWidth();
    int taskHeigth = ips.getTask().getHeight();

    if (GuiUtil.almostEqualRight(taskWidth - ips.getWidth(), ips.getX(),
        EPSILON)) {
      ips.setX(taskWidth - ips.getWidth());
    }

    if (GuiUtil.almostEqualRight(taskHeigth - ips.getHeight(), ips.getY(),
        EPSILON)) {
      ips.setY(taskHeigth - ips.getHeight());
    }

    updatePictogramElement(ips.getInternalPortLabel());
    getDiagramBehavior().refresh();
  }
}
