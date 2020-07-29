/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import java.util.Optional;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.impl.TextImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.algorithms.styles.StylesPackage;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ruminaq.gui.model.diagram.InternalPortLabelShape;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * GraphicsAlgorithm for Label.
 *
 * @author Marek Jagielski
 */
public class InternalPortLabelShapeGA extends TextImpl {

  public static final Font FONT = StylesFactory.eINSTANCE.createFont();

  static {
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Name(), IGaService.DEFAULT_FONT);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Size(),
        IGaService.DEFAULT_FONT_SIZE);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Italic(), Boolean.FALSE);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Bold(), Boolean.FALSE);
  }

  private InternalPortLabelShape shape;

  private InternalPortShape internalPortShape;

  private Position position;

  private interface Position {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    Double getRotation();
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

    @Override
    public int getWidth() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getHeight();
    }

    @Override
    public int getHeight() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getWidth();
    }

    @Override
    public Double getRotation() {
      return 90.0;
    }
  }

  private class Top implements Position {

    @Override
    public int getX() {
      return internalPortShape.getX()
          - ((getWidth() - internalPortShape.getWidth()) >> 1);
    }

    @Override
    public int getY() {
      return internalPortShape.getY() - getHeight();
    }

    @Override
    public int getWidth() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getHeight();
    }

    @Override
    public int getHeight() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getWidth();
    }

    @Override
    public Double getRotation() {
      return -90.0;
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

    @Override
    public int getWidth() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getWidth();
    }

    @Override
    public int getHeight() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getHeight();
    }

    @Override
    public Double getRotation() {
      return 0.0;
    }
  }

  private class Left implements Position {

    @Override
    public int getX() {
      return internalPortShape.getX() + getWidth();
    }

    @Override
    public int getY() {
      return internalPortShape.getY() + (internalPortShape.getWidth() >> 1);
    }

    @Override
    public int getWidth() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getWidth();
    }

    @Override
    public int getHeight() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getHeight();
    }

    @Override
    public Double getRotation() {
      return 0.0;
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

    @Override
    public int getWidth() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getWidth();
    }

    @Override
    public int getHeight() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getHeight();
    }

    @Override
    public Double getRotation() {
      return 45.0;
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

    @Override
    public int getWidth() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getWidth();
    }

    @Override
    public int getHeight() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getHeight();
    }

    @Override
    public Double getRotation() {
      return 45.0;
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

    @Override
    public int getWidth() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getWidth();
    }

    @Override
    public int getHeight() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getHeight();
    }

    @Override
    public Double getRotation() {
      return 45.0;
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

    @Override
    public int getWidth() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getWidth();
    }

    @Override
    public int getHeight() {
      return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
          .getHeight();
    }

    @Override
    public Double getRotation() {
      return 45.0;
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
    setPosition();
  }

  private void setPosition() {
    TaskShape taskShape = internalPortShape.getTask();

    if (internalPortShape.getX() == 0 && internalPortShape.getY() == 0) {
      this.position = new RightBottom();
    } else if (internalPortShape.getX() == 0 && internalPortShape
        .getY() == taskShape.getHeight() - internalPortShape.getHeight()) {
      this.position = new RightTop();
    } else if (internalPortShape.getX() == taskShape.getWidth()
        - internalPortShape.getWidth() && internalPortShape.getY() == 0) {
      this.position = new LeftBottom();
    } else if (internalPortShape.getX() == taskShape.getWidth()
        - internalPortShape.getWidth()
        && internalPortShape.getY() == taskShape.getHeight()
            - internalPortShape.getHeight()) {
      this.position = new LeftTop();
    } else if (internalPortShape.getX() == 0) {
      this.position = new Right();
    } else if (internalPortShape.getX() == taskShape.getWidth()
        - internalPortShape.getWidth()) {
      this.position = new Left();
    } else if (internalPortShape.getY() == 0) {
      this.position = new Bottom();
    } else if (internalPortShape.getY() == taskShape.getHeight()
        - internalPortShape.getHeight()) {
      this.position = new Top();
    }
  }

  @Override
  public String getValue() {
    return Optional.of(this.shape.getInternalPort())
        .map(LabeledRuminaqShape::getModelObject).map(BaseElement::getId)
        .orElse("");
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
    return position.getWidth();
  }

  @Override
  public int getHeight() {
    return position.getHeight();
  }

  @Override
  public Double getRotation() {
    return position.getRotation();
  }

  @Override
  public Font getFont() {
    return FONT;
  }

  @Override
  public Orientation getHorizontalAlignment() {
    return Orientation.UNSPECIFIED;
  }

  @Override
  public Orientation getVerticalAlignment() {
    return Orientation.UNSPECIFIED;
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
  public Double getTransparency() {
    return super.getTransparency();
  }

  @Override
  public Boolean getFilled() {
    return Boolean.TRUE;
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }
}
