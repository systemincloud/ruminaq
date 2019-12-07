package org.ruminaq.gui.model.diagram.impl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.algorithms.AlgorithmsFactory;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.algorithms.styles.StylesPackage;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

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

  public static GraphicsAlgorithm getGraphicsAlgorithm() {
    IGaService gaService = Graphiti.getGaService();
    Rectangle graphicsAlgorithm = AlgorithmsFactory.eINSTANCE.createRectangle();
    gaService.setLocationAndSize(graphicsAlgorithm, 0, 0, 1000, 1000);
    graphicsAlgorithm.setBackground(Colors.WHITE);
    return graphicsAlgorithm;
  }
}
