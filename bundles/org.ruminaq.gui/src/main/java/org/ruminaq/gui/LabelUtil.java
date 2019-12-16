/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Label .
 *
 * @author Marek Jagielski
 */
public final class LabelUtil {

  public static final String LABEL_PROPERTY = "label";
  public static final String LABEL_TRUE = Boolean.TRUE.toString();
  public static final int LABEL_POSITION_ERROR_MARGIN = 4;

  private LabelUtil() {
    // Util class
  }

  /**
   * Check if label was moved.
   *
   * @param label label pe
   * @param pe    pictogram element of labeled element
   * @return label was not moved
   */
  public static boolean isLabelInDefaultPosition(PictogramElement label,
      PictogramElement pe) {

    int shapeX = pe.getGraphicsAlgorithm().getX();
    int shapeY = pe.getGraphicsAlgorithm().getY();
    int shapeWidth = pe.getGraphicsAlgorithm().getWidth();
    int shapeHeight = pe.getGraphicsAlgorithm().getHeight();
    int textWidth = label.getGraphicsAlgorithm().getWidth();
    int currentLabelX = label.getGraphicsAlgorithm().getX();
    int currentLabelY = label.getGraphicsAlgorithm().getY();

    int newShapeX = shapeX - ((textWidth) / 2) + shapeWidth / 2;
    int newShapeY = shapeY + shapeHeight + 2;

    return GuiUtil.almostEqual(currentLabelX, newShapeX,
        LABEL_POSITION_ERROR_MARGIN)
        && GuiUtil.almostEqual(currentLabelY, newShapeY,
            LABEL_POSITION_ERROR_MARGIN);
  }
}
