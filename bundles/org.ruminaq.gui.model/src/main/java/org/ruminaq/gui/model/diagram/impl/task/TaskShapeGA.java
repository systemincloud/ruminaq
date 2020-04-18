/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import java.util.Optional;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.impl.RoundedRectangleImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;
import org.ruminaq.model.ruminaq.Task;

/**
 * GraphicsAlgorithm for Task.
 *
 * @author Marek Jagielski
 */
public class TaskShapeGA extends RoundedRectangleImpl {

  private static final int CORNER_WIDTH = 20;

  private static final int CORNER_HEIGHT = 20;

  private TaskShape shape;

  /**
   * GraphicsAlgorithm for Task.
   * 
   * @param shape parent TaskShape
   */
  public TaskShapeGA(TaskShape shape) {
    this.shape = shape;
  }

  @Override
  public int getCornerWidth() {
    return CORNER_WIDTH;
  }

  @Override
  public int getCornerHeight() {
    return CORNER_HEIGHT;
  }

  @Override
  public Color getBackground() {
    return Colors.WHITE;
  }

  @Override
  public LineStyle getLineStyle() {
    return Optional.of(shape).map(RuminaqShape::getModelObject)
        .filter(Task.class::isInstance).map(Task.class::cast)
        .filter(Task::isAtomic).map(t -> LineStyle.SOLID)
        .orElse(LineStyle.SOLID);
  }

  @Override
  public void setX(int newX) {
    shape.setX(newX);
  }

  @Override
  public int getX() {
    return shape.getX();
  }

  @Override
  public void setY(int newY) {
    shape.setY(newY);
  }

  @Override
  public int getY() {
    return shape.getY();
  }

  @Override
  public int getWidth() {
    return shape.getWidth();
  }

  @Override
  public int getHeight() {
    return shape.getHeight();
  }

  @Override
  public Integer getLineWidth() {
    return 1;
  }

  @Override
  public Color getForeground() {
    return Colors.BLACK;
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }

}
