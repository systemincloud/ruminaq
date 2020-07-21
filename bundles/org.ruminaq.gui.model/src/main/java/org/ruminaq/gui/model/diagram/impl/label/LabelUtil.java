/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.label;

import org.ruminaq.gui.model.GuiUtil;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;

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
   * @param labelShape LabelShape
   * @return label was not moved
   */
  public static boolean isInDefaultPosition(LabelShape labelShape) {
    LabeledRuminaqShape labeledShape = labelShape.getLabeledShape();

    int defaultShapeX = labeledShape.getX()
        - ((labelShape.getWidth() - labeledShape.getWidth()) >> 1);
    int defaultShapeY = labeledShape.getY() + labeledShape.getHeight()
        + SHAPE_LABEL_SPACE;

    return GuiUtil.almostEqual(labelShape.getX(), defaultShapeX,
        LABEL_POSITION_ERROR_MARGIN)
        && GuiUtil.almostEqual(labelShape.getY(), defaultShapeY,
            LABEL_POSITION_ERROR_MARGIN);
  }

  /**
   * Move label to default position.
   *
   * @param labelShape LabelShape
   */
  public static void placeInDefaultPosition(LabelShape labelShape) {
    LabeledRuminaqShape labeledShape = labelShape.getLabeledShape();
    int labelShapeX = labeledShape.getX()
        - ((labelShape.getWidth() - labeledShape.getWidth()) >> 1);
    int labelShapeY = labeledShape.getY() + labeledShape.getHeight()
        + SHAPE_LABEL_SPACE;

    labelShape.setX(labelShapeX);
    labelShape.setY(labelShapeY);
  }
}
