/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.constant.gui;

import java.util.Optional;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.impl.MultiTextImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;
import org.ruminaq.gui.model.diagram.impl.task.TaskShapeGA;
import org.ruminaq.tasks.constant.gui.constantshape.ConstantShape;
import org.ruminaq.tasks.constant.model.constant.Constant;

/**
 * GraphicsAlgorithm for Task.
 *
 * @author Marek Jagielski
 */
public class ConstantShapeGA extends TaskShapeGA {

  private static final int MARGIN = 10;

  private ConstantShape shape;

  /**
   * Additional text that can be placed below icon or name of task inside
   * TaskShape.
   */
  private class Value extends MultiTextImpl {

    @Override
    public int getWidth() {
      return shape.getWidth() - (MARGIN << 1);
    }

    @Override
    public int getHeight() {
      return shape.getHeight() - (MARGIN << 1);
    }

    @Override
    public int getX() {
      return MARGIN;
    }

    @Override
    public int getY() {
      return MARGIN + (MARGIN >> 1);
    }

    @Override
    public Orientation getHorizontalAlignment() {
      return Orientation.ALIGNMENT_CENTER;
    }

    @Override
    public Orientation getVerticalAlignment() {
      return Orientation.ALIGNMENT_MIDDLE;
    }

    @Override
    public Color getForeground() {
      return Colors.BLACK;
    }

    @Override
    public Color getBackground() {
      return Colors.WHITE;
    }

    @Override
    public String getValue() {
      return Optional.of(shape)
          .map(ConstantShape::getModelObject).filter(Constant.class::isInstance)
          .map(Constant.class::cast).map(Constant::getValue).orElse("");
    }

    @Override
    public Resource eResource() {
      return new NoResource();
    }
  }

  /**
   * GraphicsAlgorithm for ConstantShape.
   *
   * @param shape parent TaskShape
   */
  public ConstantShapeGA(ConstantShape shape) {
    super(shape);
    this.shape = shape;
    this.children = ECollections.asEList(new Value());
  }

}
