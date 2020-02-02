/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.label;

import java.util.Optional;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.impl.MultiTextImpl;
import org.eclipse.graphiti.mm.algorithms.impl.RectangleImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.algorithms.styles.StylesPackage;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * GraphicsAlgorithm for Label.
 *
 * @author Marek Jagielski
 */
public class LabelShapeGA extends RectangleImpl {

  private final class Text extends MultiTextImpl {
    @Override
    public String getValue() {
      return Optional.ofNullable(shape.getLabeledShape())
          .map(LabeledRuminaqShape::getModelObject).map(BaseElement::getId)
          .orElse("");
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
    public Boolean getFilled() {
      return Boolean.FALSE;
    }

    @Override
    public Font getFont() {
      return FONT;
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
    public Double getTransparency() {
      return 0D;
    }

    @Override
    public int getHeight() {
      return GraphitiUi.getUiLayoutService()
          .calculateTextSize(getValue(), FONT).getHeight() + TEXT_PADDING;
    }

    @Override
    public int getWidth() {
      return GraphitiUi.getUiLayoutService()
          .calculateTextSize(getValue(), FONT).getWidth() + TEXT_PADDING;
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

  public static final Font FONT = StylesFactory.eINSTANCE.createFont();

  static {
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Name(), IGaService.DEFAULT_FONT);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Size(),
        IGaService.DEFAULT_FONT_SIZE);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Italic(), Boolean.FALSE);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Bold(), Boolean.FALSE);
  }
  
  private static final int TEXT_PADDING = 5;
  
  private EList<GraphicsAlgorithm> children;

  private LabelShape shape;

  private Text text = new Text();

  public LabelShapeGA(LabelShape shape) {
    this.shape = shape;
    this.children = ECollections.asEList(text);
  }

  @Override
  public int getX() {
    return shape.getX();
  }

  @Override
  public int getY() {
    return shape.getY();
  }

  @Override
  public void setX(int newX) {
    shape.setX(newX);
  }

  @Override
  public void setY(int newY) {
    shape.setY(newY);
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
  public Boolean getFilled() {
    return Boolean.FALSE;
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
    return Boolean.FALSE;
  }

  @Override
  public Double getTransparency() {
    return 0D;
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
