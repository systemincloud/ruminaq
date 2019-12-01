package org.ruminaq.gui.model.diagram.impl;

import org.eclipse.emf.ecore.EObject;

public class CustomRuminaqDiagramImpl {

  public static String getDiagramTypeId() {
    return "Ruminaq";
  }

  public static boolean isVisible() {
    return true;
  }

  public static int getGridUnit() {
    return -1;
  }

  public static String getName(EObject diagram) {
    return diagram.eResource().getURI().trimFileExtension().lastSegment();
  }

  public static boolean isSnapToGrid() {
    return false;
  }
}
