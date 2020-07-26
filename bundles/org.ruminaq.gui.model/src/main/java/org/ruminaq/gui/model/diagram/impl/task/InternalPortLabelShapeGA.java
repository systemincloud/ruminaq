/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import java.util.Optional;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.impl.RectangleImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.ruminaq.gui.model.diagram.InternalPortLabelShape;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;
import org.ruminaq.gui.model.diagram.impl.label.Text;

/**
 * GraphicsAlgorithm for Label.
 *
 * @author Marek Jagielski
 */
public class InternalPortLabelShapeGA extends RectangleImpl {

  private EList<GraphicsAlgorithm> children;

  private InternalPortLabelShape shape;

  private InternalPortShape internalPortShape;

  private Text text;

  private Position position;

  private interface Position {
    int getX();

    int getY();
  }

  private class Bottom implements Position {

    @Override
    public int getX() {
      return internalPortShape.getX() + (internalPortShape.getWidth() >> 1);
    }

    @Override
    public int getY() {
      return internalPortShape.getY() + internalPortShape.getHeight();
    }

  }

  private class Top implements Position {

    @Override
    public int getX() {
      return internalPortShape.getX() - internalPortShape.getWidth();
    }

    @Override
    public int getY() {
      return internalPortShape.getY() - text.getWidth() + 10;
    }

  }

  private class Right implements Position {

    @Override
    public int getX() {
      return internalPortShape.getX() + internalPortShape.getWidth();
    }

    @Override
    public int getY() {
      return internalPortShape.getY() + (internalPortShape.getHeight() >> 1);
    }

  }

  private class Left implements Position {

    @Override
    public int getX() {
      return internalPortShape.getX() + text.getWidth();
    }

    @Override
    public int getY() {
      return internalPortShape.getY() + (internalPortShape.getWidth() >> 1);
    }

  }

  private class RightTop implements Position {

    @Override
    public int getX() {
      return 0;
    }

    @Override
    public int getY() {
      return 0;
    }

  }

  private class RightBottom implements Position {

    @Override
    public int getX() {
      return 0;
    }

    @Override
    public int getY() {
      return 0;
    }

  }

  private class LeftTop implements Position {

    @Override
    public int getX() {
      return 0;
    }

    @Override
    public int getY() {
      return 0;
    }

  }

  private class LeftBottom implements Position {

    @Override
    public int getX() {
      return 0;
    }

    @Override
    public int getY() {
      return 0;
    }

  }

  /**
   * GraphicsAlgorithm for Label.
   *
   * @param shape InternalPortLabelShape
   */
  public InternalPortLabelShapeGA(InternalPortLabelShape shape) {
    this.shape = shape;
    this.internalPortShape = shape.getInternalPort();
    this.text = Optional.of(this.shape.getInternalPort())
        .map(LabeledRuminaqShape::getModelObject).map(Text::new).orElseThrow();
    this.children = ECollections.asEList(text);
    setPosition();
  }

  private void setPosition() {
    TaskShape taskShape = internalPortShape.getTask();

    if (internalPortShape.getX() == 0 && internalPortShape.getY() == 0) {
      this.position = new RightBottom();
      this.text.setRotation(45.0);
    } else if (internalPortShape.getX() == 0 && internalPortShape
        .getY() == taskShape.getHeight() - internalPortShape.getHeight()) {
      this.position = new RightTop();
      this.text.setRotation(45.0);
    } else if (internalPortShape.getX() == taskShape.getWidth()
        - internalPortShape.getWidth() && internalPortShape.getY() == 0) {
      this.position = new LeftBottom();
      this.text.setRotation(45.0);
    } else if (internalPortShape.getX() == taskShape.getWidth()
        - internalPortShape.getWidth()
        && internalPortShape.getY() == taskShape.getHeight()
            - internalPortShape.getHeight()) {
      this.position = new LeftTop();
      this.text.setRotation(45.0);
    } else if (internalPortShape.getX() == 0) {
      this.position = new Right();
      this.text.setRotation(0.0);
    } else if (internalPortShape.getX() == taskShape.getWidth()
        - internalPortShape.getWidth()) {
      this.position = new Left();
      this.text.setRotation(0.0);
    } else if (internalPortShape.getY() == 0) {
      this.position = new Bottom();
      this.text.setRotation(90.0);
    } else if (internalPortShape.getY() == taskShape.getHeight()
        - internalPortShape.getHeight()) {
      this.position = new Top();
      this.text.setRotation(-90.0);
    }
  }

  @Override
  public void setX(int newX) {
  }

  @Override
  public void setY(int newY) {
  }

  @Override
  public int getX() {
    return position.getX();
  }

  @Override
  public int getY() {
    return position.getY();
  }

  @Override
  public int getWidth() {
    return text.getWidth();
  }

  @Override
  public int getHeight() {
    return text.getHeight();
  }

  @Override
  public Integer getLineWidth() {
    return 1;
  }

  @Override
  public LineStyle getLineStyle() {
    return LineStyle.SOLID;
  }

  @Override
  public Boolean getLineVisible() {
    return Boolean.TRUE;
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
  public EList<GraphicsAlgorithm> getGraphicsAlgorithmChildren() {
    return ECollections.unmodifiableEList(children);
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }
}
