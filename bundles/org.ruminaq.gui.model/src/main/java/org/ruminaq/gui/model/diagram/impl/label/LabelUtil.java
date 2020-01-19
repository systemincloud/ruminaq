/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.label;

import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.impl.GuiUtil;

/**
 * Label util class.
 *
 * @author Marek Jagielski
 */
public final class LabelUtil {

  private static final int SHAPE_LABEL_SPACE = 2;
  
  private static final int LABEL_POSITION_ERROR_MARGIN = 4;
  
  private LabelUtil() {
    // Util class
  }

  /**
   * Check if label was moved from default position.
   *
   * @param labelShape
   * @return label was not moved
   */
  public static boolean isInDefaultPosition(LabelShape labelShape) {
    LabeledRuminaqShape labeledShape = labelShape.getLabeledShape();

    int shapeX = labeledShape.getX();
    int shapeY = labeledShape.getY();
    int shapeWidth = labeledShape.getWidth();
    int shapeHeight = labeledShape.getHeight();
    int textWidth = labelShape.getWidth();
    int currentLabelX = labelShape.getX();
    int currentLabelY = labelShape.getY();

    int newShapeX = shapeX - ((textWidth) / 2) + shapeWidth / 2;
    int newShapeY = shapeY + shapeHeight + 2;

    return GuiUtil.almostEqual(currentLabelX, newShapeX,
        LABEL_POSITION_ERROR_MARGIN)
        && GuiUtil.almostEqual(currentLabelY, newShapeY,
            LABEL_POSITION_ERROR_MARGIN);
  }
  
  /**
   * Move label to default position.
   *
   * @param labelShape
   */
  public static void placeInDefaultPosition(LabelShape labelShape) {
    LabeledRuminaqShape labeledShape = labelShape.getLabeledShape();
    int labelShapeX = labeledShape.getX() - (labelShape.getWidth() >> 1)
        + (labeledShape.getWidth() >> 1);
    int labelShapeY = labeledShape.getY() + labeledShape.getHeight()
        + SHAPE_LABEL_SPACE;

    labelShape.setX(labelShapeX);
    labelShape.setY(labelShapeY);
  }
}
