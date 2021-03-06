/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
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

  private static final int CORNER = 20;

  public static final int ICON_SIZE = 44;

  private TaskShape shape;

  protected EList<GraphicsAlgorithm> children = ECollections.newBasicEList();

  /**
   * Optional icon in the center of TaskShape.
   */
  private class Icon extends ImageImpl {

    private String iconId;

    Icon(String id) {
      this.iconId = id;
    }

    @Override
    public String getId() {
      return iconId;
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

  /**
   * If icon not present the name of task should be displayed.
   */
  private class Name extends MultiTextImpl {

    @Override
    public int getWidth() {
      return shape.getWidth() - (CORNER << 1);
    }

    @Override
    public int getHeight() {
      return shape.getHeight() - (CORNER << 1);
    }

    @Override
    public int getX() {
      return CORNER;
    }

    @Override
    public int getY() {
      return CORNER;
    }

    @Override
    public Color getBackground() {
      return Colors.WHITE;
    }

    @Override
    public Orientation getHorizontalAlignment() {
      return Orientation.ALIGNMENT_CENTER;
    }

    @Override
    public Orientation getVerticalAlignment() {
      return Orientation.ALIGNMENT_CENTER;
    }

    @Override
    public String getValue() {
      return ModelUtil.getName(shape.getModelObject().getClass()).replace(' ',
          '\n');
    }

    @Override
    public Resource eResource() {
      return new NoResource();
    }
  }

  /**
   * Additional text that can be placed below icon or name of task inside
   * TaskShape.
   */
  private class Description extends TextImpl {

    @Override
    public int getWidth() {
      return shape.getWidth() - (CORNER << 1);
    }

    @Override
    public int getHeight() {
      return ((shape.getHeight() -  ICON_SIZE) >> 1) - 1;
    }

    @Override
    public int getX() {
      return CORNER;
    }

    @Override
    public int getY() {
      return (shape.getHeight() + ICON_SIZE) >> 1;
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
      return shape.getDescription();
    }

    @Override
    public Color getBackground() {
      return Colors.WHITE;
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
    this.children.add(Optional.of(shape).map(TaskShape::getIconId)
        .filter(Objects::nonNull).map(Icon::new)
        .map(GraphicsAlgorithm.class::cast).orElseGet(Name::new));
    Optional.of(shape).map(s -> new Description())
        .ifPresent(this.children::add);
  }

  @Override
  public int getCornerWidth() {
    return CORNER;
  }

  @Override
  public int getCornerHeight() {
    return getCornerWidth();
  }

  @Override
  public Color getBackground() {
    return Colors.WHITE;
  }

  @Override
  public LineStyle getLineStyle() {
    return Optional.of(shape).map(RuminaqShape::getModelObject)
        .filter(Task.class::isInstance).map(Task.class::cast)
        .filter(Predicate.not(Task::isAtomic)).map(t -> LineStyle.DASH)
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
