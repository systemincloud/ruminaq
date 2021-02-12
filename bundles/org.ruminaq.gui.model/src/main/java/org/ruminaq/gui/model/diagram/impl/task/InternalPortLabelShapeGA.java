/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import java.util.Optional;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.datatypes.IDimension;
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
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * GraphicsAlgorithm for Label.
 *
 * @author Marek Jagielski
 */
public class InternalPortLabelShapeGA extends TextImpl {

  private static double ROTATION_HORIZONTAL = 0.0;

  private static double ROTATION_VERTICAL = -90.0;

  private static double TRANSPARENCY_LABEL = 0.5;

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
    boolean isInPosition();

    int getX();

    int getY();

    int getWidth();

    int getHeight();

    Double getRotation();
  }

  private class Bottom implements Position {

    @Override
    public boolean isInPosition() {
      return internalPortShape.getY() == 0 && internalPortShape.getX() != 0
          && internalPortShape.getX() != internalPortShape.getTask().getWidth()
              - internalPortShape.getWidth();
    }

    @Override
    public int getX() {
      return internalPortShape.getX()
          - ((getWidth() - internalPortShape.getWidth()) >> 1);
    }

    @Override
    public int getY() {
      return internalPortShape.getHeight();
    }

    @Override
    public int getWidth() {
      return valueTextSize().getHeight();
    }

    @Override
    public int getHeight() {
      return valueTextSize().getWidth();
    }

    @Override
    public Double getRotation() {
      return ROTATION_VERTICAL;
    }
  }

  private class Top implements Position {

    @Override
    public boolean isInPosition() {
      return internalPortShape.getY() == internalPortShape.getTask().getHeight()
          - internalPortShape.getHeight() && internalPortShape.getX() != 0
          && internalPortShape.getX() != internalPortShape.getTask().getWidth()
              - internalPortShape.getWidth();
    }

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
      return valueTextSize().getHeight();
    }

    @Override
    public int getHeight() {
      return valueTextSize().getWidth();
    }

    @Override
    public Double getRotation() {
      return ROTATION_VERTICAL;
    }
  }

  private class Right implements Position {

    @Override
    public boolean isInPosition() {
      return internalPortShape.getX() == 0 && internalPortShape.getY() != 0
          && internalPortShape.getY() != internalPortShape.getTask().getHeight()
              - internalPortShape.getHeight();
    }

    @Override
    public int getX() {
      return internalPortShape.getWidth();
    }

    @Override
    public int getY() {
      return internalPortShape.getY()
          - ((getHeight() - internalPortShape.getHeight()) >> 1);
    }

    @Override
    public int getWidth() {
      return valueTextSize().getWidth();
    }

    @Override
    public int getHeight() {
      return valueTextSize().getHeight();
    }

    @Override
    public Double getRotation() {
      return ROTATION_HORIZONTAL;
    }
  }

  private class Left implements Position {

    @Override
    public boolean isInPosition() {
      return internalPortShape.getX() == internalPortShape.getTask().getWidth()
          - internalPortShape.getWidth()
          && internalPortShape.getY() != internalPortShape.getTask().getHeight()
              - internalPortShape.getHeight();
    }

    @Override
    public int getX() {
      return internalPortShape.getX() - getWidth();
    }

    @Override
    public int getY() {
      return internalPortShape.getY()
          - ((getHeight() - internalPortShape.getWidth()) >> 1);
    }

    @Override
    public int getWidth() {
      return valueTextSize().getWidth();
    }

    @Override
    public int getHeight() {
      return valueTextSize().getHeight();
    }

    @Override
    public Double getRotation() {
      return ROTATION_HORIZONTAL;
    }
  }

  private class RightTop implements Position {

    @Override
    public boolean isInPosition() {
      return internalPortShape.getX() == 0
          && internalPortShape.getY() == internalPortShape.getTask().getHeight()
              - internalPortShape.getHeight();
    }

    @Override
    public int getX() {
      return internalPortShape.getWidth();
    }

    @Override
    public int getY() {
      return internalPortShape.getY() - getHeight();
    }

    @Override
    public int getWidth() {
      return valueTextSize().getWidth();
    }

    @Override
    public int getHeight() {
      return valueTextSize().getHeight();
    }

    @Override
    public Double getRotation() {
      return ROTATION_HORIZONTAL;
    }

  }

  private class RightBottom implements Position {

    @Override
    public boolean isInPosition() {
      return internalPortShape.getX() == 0 && internalPortShape.getY() == 0;
    }

    @Override
    public int getX() {
      return internalPortShape.getWidth();
    }

    @Override
    public int getY() {
      return internalPortShape.getHeight();
    }

    @Override
    public int getWidth() {
      return valueTextSize().getWidth();
    }

    @Override
    public int getHeight() {
      return valueTextSize().getHeight();
    }

    @Override
    public Double getRotation() {
      return ROTATION_HORIZONTAL;
    }

  }

  private class LeftTop implements Position {

    @Override
    public boolean isInPosition() {
      return internalPortShape.getX() == internalPortShape.getTask().getWidth()
          - internalPortShape.getWidth()
          && internalPortShape.getY() == internalPortShape.getTask().getHeight()
              - internalPortShape.getHeight();
    }

    @Override
    public int getX() {
      return internalPortShape.getX() - getWidth();
    }

    @Override
    public int getY() {
      return internalPortShape.getY() - getHeight();
    }

    @Override
    public int getWidth() {
      return valueTextSize().getWidth();
    }

    @Override
    public int getHeight() {
      return valueTextSize().getHeight();
    }

    @Override
    public Double getRotation() {
      return ROTATION_HORIZONTAL;
    }

  }

  private class LeftBottom implements Position {

    @Override
    public boolean isInPosition() {
      return internalPortShape.getX() == internalPortShape.getTask().getWidth()
          - internalPortShape.getWidth() && internalPortShape.getY() == 0;
    }

    @Override
    public int getX() {
      return internalPortShape.getX() - getWidth();
    }

    @Override
    public int getY() {
      return internalPortShape.getHeight();
    }

    @Override
    public int getWidth() {
      return valueTextSize().getWidth();
    }

    @Override
    public int getHeight() {
      return valueTextSize().getHeight();
    }

    @Override
    public Double getRotation() {
      return ROTATION_HORIZONTAL;
    }

  }

  private IDimension valueTextSize() {
    return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT);
  }

  /**
   * GraphicsAlgorithm for Label.
   *
   * @param shape InternalPortLabelShape
   */
  public InternalPortLabelShapeGA(InternalPortLabelShape shape) {
    this.shape = shape;
    this.internalPortShape = shape.getInternalPort();
    updatePosition();
  }

  public void updatePosition() {
    if (new RightBottom().isInPosition()) {
      this.position = new RightBottom();
    } else if (new RightTop().isInPosition()) {
      this.position = new RightTop();
    } else if (new LeftBottom().isInPosition()) {
      this.position = new LeftBottom();
    } else if (new LeftTop().isInPosition()) {
      this.position = new LeftTop();
    } else if (new Right().isInPosition()) {
      this.position = new Right();
    } else if (new Left().isInPosition()) {
      this.position = new Left();
    } else if (new Bottom().isInPosition()) {
      this.position = new Bottom();
    } else if (new Top().isInPosition()) {
      this.position = new Top();
    }
  }

  public boolean isInPosition() {
    return position.isInPosition();
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
    return TRANSPARENCY_LABEL;
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
