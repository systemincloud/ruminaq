/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.impl.ImageImpl;
import org.eclipse.graphiti.mm.algorithms.impl.MultiTextImpl;
import org.eclipse.graphiti.mm.algorithms.impl.RoundedRectangleImpl;
import org.eclipse.graphiti.mm.algorithms.impl.TextImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.Task;

/**
 * GraphicsAlgorithm for Task.
 *
 * @author Marek Jagielski
 */
public class TaskShapeGA extends RoundedRectangleImpl {

  private static final int CORNER_WIDTH = 20;

  private static final int CORNER_HEIGHT = 20;

  public static final int ICON_SIZE = 44;

  private TaskShape shape;

  private EList<GraphicsAlgorithm> children;

  private class Icon extends ImageImpl {

    private String id;

    Icon(String id) {
      this.id = id;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public int getWidth() {
      return ICON_SIZE;
    }

    @Override
    public int getHeight() {
      return ICON_SIZE;
    }

    @Override
    public int getX() {
      return (shape.getWidth() - ICON_SIZE) >> 1;
    }

    @Override
    public int getY() {
      return (shape.getHeight() - ICON_SIZE) >> 1;
    }

    @Override
    public Boolean getProportional() {
      return Boolean.FALSE;
    }

    @Override
    public Boolean getStretchH() {
      return Boolean.FALSE;
    }

    @Override
    public Boolean getStretchV() {
      return Boolean.FALSE;
    }

    @Override
    public Resource eResource() {
      return new NoResource();
    }
  }

  private class Name extends MultiTextImpl {

    @Override
    public int getWidth() {
      return shape.getWidth();
    }

    @Override
    public int getHeight() {
      return shape.getHeight();
    }

    @Override
    public int getX() {
      return 0;
    }

    @Override
    public int getY() {
      return 0;
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
    public String getValue() {
      return ModelUtil.getName(shape.getModelObject().getClass()).replace(" ",
          "\n");
    }

    @Override
    public Resource eResource() {
      return new NoResource();
    }
  }

  private class Description extends TextImpl {

    private String text;

    Description(String text) {
      this.text = text;
    }

    @Override
    public int getWidth() {
      return shape.getWidth();
    }

    @Override
    public int getHeight() {
      return shape.getHeight() - ICON_SIZE;
    }

    @Override
    public int getX() {
      return 0;
    }

    @Override
    public int getY() {
      return ICON_SIZE;
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
    public String getValue() {
      return text;
    }

    @Override
    public Resource eResource() {
      return new NoResource();
    }
  }

  /**
   * GraphicsAlgorithm for Task.
   * 
   * @param shape parent TaskShape
   */
  public TaskShapeGA(TaskShape shape) {
    this.shape = shape;
    this.children = ECollections.asEList(Optional.of(shape)
        .map(TaskShape::getIconId).filter(Objects::nonNull).map(Icon::new)
        .map(GraphicsAlgorithm.class::cast).orElseGet(Name::new));
    Optional.of(shape).map(TaskShape::getDescription).map(Description::new)
        .ifPresent(this.children::add);
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
  public EList<GraphicsAlgorithm> getGraphicsAlgorithmChildren() {
    return ECollections.unmodifiableEList(children);
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }

}
