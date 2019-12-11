package org.ruminaq.gui.model.diagram.impl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.algorithms.AlgorithmsFactory;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

public final class CustomRuminaqDiagramImpl {

  private static final String ID = "Ruminaq";
  
  private static final int WIDTH = 1000;
  
  private static final int HEIGHT = 1000;
  
  public static String getDiagramTypeId() {
    return ID;
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
    gaService.setLocationAndSize(graphicsAlgorithm, 0, 0, WIDTH, HEIGHT);
    graphicsAlgorithm.setBackground(Colors.WHITE);
    return graphicsAlgorithm;
  }
  
  private CustomRuminaqDiagramImpl() {
    // only static methods
  }
}
