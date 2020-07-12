/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.gui.model.diagram.impl.task;

import org.ruminaq.gui.model.diagram.InternalPortLabelShape;

/**
 * InternalPortLabelUtil util class.
 *
 * @author Marek Jagielski
 */
public final class InternalPortLabelUtil {

  private InternalPortLabelUtil() {
    // Util class
  }

  /**
   * Check if label is not updated to default position.
   *
   * @param labelShape InternalPortLabelUtil
   * @return label was not moved
   */
  public static boolean isInDefaultPosition(InternalPortLabelShape labelShape) {
//    LabeledRuminaqShape labeledShape = labelShape.getLabeledShape();
//
//    int defaultShapeX = labeledShape.getX() - ((labelShape.getWidth()) >> 1)
//        + (labeledShape.getWidth() >> 1);
//    int defaultShapeY = labeledShape.getY() + labeledShape.getHeight()
//        + SHAPE_LABEL_SPACE;
//
//    return GuiUtil.almostEqual(labelShape.getX(), defaultShapeX,
//        LABEL_POSITION_ERROR_MARGIN)
//        && GuiUtil.almostEqual(labelShape.getY(), defaultShapeY,
//            LABEL_POSITION_ERROR_MARGIN);
    return true;
  }

  /**
   * Move label to default position.
   *
   * @param labelShape InternalPortLabelUtil
   */
  public static void placeInDefaultPosition(InternalPortLabelShape labelShape) {
//    LabeledRuminaqShape labeledShape = labelShape.getLabeledShape();
//    int labelShapeX = labeledShape.getX() - (labelShape.getWidth() >> 1)
//        + (labeledShape.getWidth() >> 1);
//    int labelShapeY = labeledShape.getY() + labeledShape.getHeight()
//        + SHAPE_LABEL_SPACE;
//
//    labelShape.setX(labelShapeX);
//    labelShape.setY(labelShapeY);
  }
}
