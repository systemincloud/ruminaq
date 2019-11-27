package org.ruminaq.gui.model.diagram.impl;

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

  public static boolean isSnapToGrid() {
    return false;
  }
}
