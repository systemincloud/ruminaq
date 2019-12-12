/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.factories;

import java.util.WeakHashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.impl.RoundedRectangleImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.model.diagram.PortShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;

public class PortShapeFactory implements Factory {

  public static final PortShapeFactory INSTANCE = new PortShapeFactory();

  private static final int CORNER = 20;

  private WeakHashMap<EObject, PortShapeGA> cacheGraphicsAlgorithms = new WeakHashMap<>();

  public static class PortShapeGA extends RoundedRectangleImpl {

    private PortShape shape;

    PortShapeGA(PortShape shape) {
      this.shape = shape;
    }
    
    @Override
    public int getCornerWidth() {
      return CORNER;
    }
    
    @Override
    public int getCornerHeight() {
      return CORNER;
    }
    
    @Override
    public Color getBackground() {
      return Colors.WHITE;
    }
    
    @Override
    public LineStyle getLineStyle() {
      return LineStyle.SOLID;
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
    public int getWidth() {
      return shape.getWidth();
    }

    @Override
    public int getHeight() {
      return shape.getHeight();
    }

    @Override
    public Integer getLineWidth() {
      return shape.getLineWidth();
    }

    @Override
    public Resource eResource() {
      return new NoResource();
    }
  }

  @Override
  public boolean isForThisShape(Shape shape) {
    return PortShape.class.isAssignableFrom(shape.getClass());
  }

  @Override
  public GraphicsAlgorithm getGA(Shape shape) {
    EObject bo = shape.getLink().getBusinessObjects().get(0);
    if (!cacheGraphicsAlgorithms.containsKey(bo)) {
      cacheGraphicsAlgorithms.put(bo, new PortShapeGA((PortShape) shape));
    }
    return cacheGraphicsAlgorithms.get(bo);
  }
}
