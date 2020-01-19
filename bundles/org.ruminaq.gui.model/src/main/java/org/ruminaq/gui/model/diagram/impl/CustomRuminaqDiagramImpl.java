/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.algorithms.AlgorithmsFactory;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

/**
 * Dynamic GraphicsAlgorithm for Diagram.
 * 
 * @author Marek Jagielski
 */
public final class CustomRuminaqDiagramImpl {

  public static final String DIAGRAM_TYPE_ID = "Ruminaq";
  
  public static final int GRID_UNIT = -1;

  private static final int WIDTH = 1000;
  
  private static final int HEIGHT = 1000;
  
  private CustomRuminaqDiagramImpl() {
    // only static methods
  }

  public static String getName(EObject diagram) {
    return diagram.eResource().getURI().trimFileExtension().lastSegment();
  }

  public static boolean isSnapToGrid() {
    return false;
  }

  /**
   * Used by RuminaqDiagram.
   */
  public static GraphicsAlgorithm getGraphicsAlgorithm() {
    IGaService gaService = Graphiti.getGaService();
    Rectangle graphicsAlgorithm = AlgorithmsFactory.eINSTANCE.createRectangle();
    gaService.setLocationAndSize(graphicsAlgorithm, 0, 0, WIDTH, HEIGHT);
    graphicsAlgorithm.setBackground(Colors.WHITE);
    return graphicsAlgorithm;
  }
}
