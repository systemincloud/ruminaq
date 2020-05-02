/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.impl.RoundedRectangleImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;

/**
 * GraphicsAlgorithm for InternalOutputPort.
 *
 * @author Marek Jagielski
 */
public class InternalPortShapeGA extends RoundedRectangleImpl {
  
  private static final int CORNER = 5;
  
  protected InternalPortShape shape;

  public InternalPortShapeGA(InternalPortShape shape) {
    this.shape = shape;
  }

  @Override
  public int getWidth() {
    return shape.getWidth();
  }

  @Override
  public int getHeight() {
    return shape.getHeight();
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
  public Color getForeground() {
    return Colors.BLACK;
  }

  @Override
  public Color getBackground() {
    return Colors.WHITE;
  }
  
  @Override
  public int getCornerWidth() {
    return CORNER;
  }

  @Override
  public int getCornerHeight() {
    return getCornerWidth();
  }
  
  @Override
  public Integer getLineWidth() {
    return 1;
  }
  
  @Override
  public LineStyle getLineStyle() {
    return LineStyle.SOLID;
  }
  
  @Override
  public EList<GraphicsAlgorithm> getGraphicsAlgorithmChildren() {
    return ECollections.emptyEList();
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }
}
