/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.gui.model.diagram.impl;

import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.algorithms.styles.StylesPackage;

/**
 * Graphiti color constants.
 *
 * @author Marek Jagielski
 */
public final class Colors {

  public static final int V0 = 0;
  public static final int V255 = 255;

  public static final Color WHITE = StylesFactory.eINSTANCE.createColor();
  public static final Color BLACK = StylesFactory.eINSTANCE.createColor();
  public static final Color MAGENTA = StylesFactory.eINSTANCE.createColor();

  static {
    WHITE.eSet(StylesPackage.eINSTANCE.getColor_Red(), V255);
    WHITE.eSet(StylesPackage.eINSTANCE.getColor_Green(), V255);
    WHITE.eSet(StylesPackage.eINSTANCE.getColor_Blue(), V255);

    BLACK.eSet(StylesPackage.eINSTANCE.getColor_Red(), V0);
    BLACK.eSet(StylesPackage.eINSTANCE.getColor_Green(), V0);
    BLACK.eSet(StylesPackage.eINSTANCE.getColor_Blue(), V0);

    MAGENTA.eSet(StylesPackage.eINSTANCE.getColor_Red(), V255);
    MAGENTA.eSet(StylesPackage.eINSTANCE.getColor_Green(), V0);
    MAGENTA.eSet(StylesPackage.eINSTANCE.getColor_Blue(), V255);
  }

  private Colors() {
  }
}
