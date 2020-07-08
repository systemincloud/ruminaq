/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.gui.model.diagram.impl.label;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.impl.MultiTextImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.algorithms.styles.StylesPackage;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * Label text.
 *
 * @author Marek Jagielski
 */
public final class Text extends MultiTextImpl {

  public static final Font FONT = StylesFactory.eINSTANCE.createFont();

  static {
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Name(), IGaService.DEFAULT_FONT);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Size(),
        IGaService.DEFAULT_FONT_SIZE);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Italic(), Boolean.FALSE);
    FONT.eSet(StylesPackage.eINSTANCE.getFont_Bold(), Boolean.FALSE);
  }

  private static final int TEXT_PADDING = 5;

  private final BaseElement modelObject;

  /**
   * Label text. Text value is bind
   * with BaseElement id.
   *
   * @param modelObject ruminaq object
   */
  public Text(BaseElement modelObject) {
    this.modelObject = modelObject;
  }

  @Override
  public String getValue() {
    return modelObject.getId();
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
  public Font getFont() {
    return FONT;
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
