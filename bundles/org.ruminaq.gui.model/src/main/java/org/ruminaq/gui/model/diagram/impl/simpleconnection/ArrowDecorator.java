package org.ruminaq.gui.model.diagram.impl.simpleconnection;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.impl.PolygonImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.impl.ConnectionDecoratorImpl;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;

public class ArrowDecorator extends ConnectionDecoratorImpl {

  private GraphicsAlgorithm ga = new PolygonImpl() {
    
    EList<Point> points;
    
    {
      int xy[] = new int[] { -10, -5, 0, 0, -10, 5, -8, 0 };
      int beforeAfter[] = new int[] { 3, 3, 0, 0, 3, 3, 3, 3 };
      this.points = new BasicEList<Point>(
          Graphiti.getGaCreateService().createPointList(xy, beforeAfter));
    }

    @Override
    public EList<Point> getPoints() {
      return points;
    }
    
    @Override
    public Integer getLineWidth() {
      return 1;
    }
    
    @Override
    public Double getTransparency() {
      return 0d;
    }
    
    @Override
    public Color getForeground() {
      return Colors.BLACK;
    }
    
    @Override
    public Color getBackground() {
      return Colors.BLACK;
    }

    @Override
    public Resource eResource() {
      return new NoResource();
    }
  };

  public ArrowDecorator(SimpleConnectionShape shape) {
  }

  @Override
  public boolean isVisible() {
    return true;
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }

  @Override
  public boolean isLocationRelative() {
    return true;
  }

  @Override
  public double getLocation() {
    return 1.0;
  }

  @Override
  public GraphicsAlgorithm getGraphicsAlgorithm() {
    return ga;
  }
}
