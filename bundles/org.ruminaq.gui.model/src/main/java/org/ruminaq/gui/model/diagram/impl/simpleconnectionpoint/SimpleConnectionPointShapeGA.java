/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.simpleconnectionpoint;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.impl.EllipseImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;

/**
 * GraphicsAlgorithm for SimpleConnectionPoint.
 *
 * @author Marek Jagielski
 */
public class SimpleConnectionPointShapeGA extends EllipseImpl {
  
  public static final int POINT_SIZE = 9;

  private SimpleConnectionPointShape shape;

  /**
   * GraphicsAlgorithm for SimpleConnectionPoint.
   * 
   * @param shape parent SimpleConnectionPointShape
   */
  public SimpleConnectionPointShapeGA(SimpleConnectionPointShape shape) {
    this.shape = shape;
  }
  
  @Override
  public void setX(int newX) {
    shape.setX(newX);
  }

  @Override
  public int getX() {
    return shape.getX();
  }

  @Override
  public void setY(int newY) {
    shape.setY(newY);
  }

  @Override
  public int getY() {
    return shape.getY();
  }
  
  @Override
  public int getHeight() {
    return shape.getPointSize();
  }
  
  @Override
  public int getWidth() {
    return shape.getPointSize();
  }
  
  @Override
  public Color getBackground() {
    return Colors.BLACK;
  }
  
  @Override
  public Color getForeground() {
    return Colors.BLACK;
  }
  
  @Override
  public Resource eResource() {
    return new NoResource();
  }
}
