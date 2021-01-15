/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.label;

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
public class LabelShapeGA extends TextImpl {

  public static final Font FONT = StylesFactory.eINSTANCE.createFont();

  static {
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Name(), IGaService.DEFAULT_FONT);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Size(),
        IGaService.DEFAULT_FONT_SIZE);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Italic(), Boolean.FALSE);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Bold(), Boolean.FALSE);
  }

  private static final int TEXT_PADDING = 5;
  
  private static final double TRANSPARENCY = 0.4D;

  private LabelShape shape;

  /**
   * GraphicsAlgorithm for Label.
   *
   * @param shape LabelShape
   */
  public LabelShapeGA(LabelShape shape) {
    this.shape = shape;
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
  public String getValue() {
    return Optional.of(this.shape.getLabeledShape())
        .map(LabeledRuminaqShape::getModelObject).map(BaseElement::getId)
        .orElseThrow();
  }

  @Override
  public Font getFont() {
    return FONT;
  }

  @Override
  public int getHeight() {
    return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
        .getHeight() + TEXT_PADDING;
  }

  @Override
  public int getWidth() {
    return GraphitiUi.getUiLayoutService().calculateTextSize(getValue(), FONT)
        .getWidth() + TEXT_PADDING;
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
  public Double getTransparency() {
    return TRANSPARENCY;
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
  public Resource eResource() {
    return new NoResource();
  }
}
