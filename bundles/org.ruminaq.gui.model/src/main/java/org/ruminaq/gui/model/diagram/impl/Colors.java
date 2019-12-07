package org.ruminaq.gui.model.diagram.impl;

import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.algorithms.styles.StylesPackage;

public final class Colors {

  public static final int V255 = 255;
  
  public static final Color WHITE = StylesFactory.eINSTANCE.createColor();
  
  static {
    WHITE.eSet(StylesPackage.eINSTANCE.getColor_Red(), V255);
    WHITE.eSet(StylesPackage.eINSTANCE.getColor_Green(), V255);
    WHITE.eSet(StylesPackage.eINSTANCE.getColor_Blue(), V255);
  }
  
  private Colors() {
  }
}
