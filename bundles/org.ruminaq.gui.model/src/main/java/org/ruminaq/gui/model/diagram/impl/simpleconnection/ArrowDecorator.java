/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.simpleconnection;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.impl.PolygonImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.pictograms.impl.ConnectionDecoratorImpl;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;

/**
 * Arrow on SimpleConnectionShape.
 *
 * @author Marek Jagielski
 */
public class ArrowDecorator extends ConnectionDecoratorImpl {

  private static final int[] XY = new int[] { -10, -5, 0, 0, -10, 5, -8, 0 };

  private static final int[] BEFORE_AFTER = new int[] { 3, 3, 0, 0, 3, 3, 3,
      3 };

  /**
   * Triangle made from polygon. Should have the same style as
   * SimpleConnectionShape.
   */
  private static final class Arrow extends PolygonImpl {

    private Arrow() {
      this.points = new BasicEList<>(
          Graphiti.getGaCreateService().createPointList(XY, BEFORE_AFTER));
    }

    @Override
    public Integer getLineWidth() {
      return 1;
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
  }

  private SimpleConnectionShape shape;

  private GraphicsAlgorithm ga = new Arrow();

  /**
   * Arrow on SimpleConnectionShape.
   *
   * @param shape parent SimpleConnectionShape
   */
  public ArrowDecorator(SimpleConnectionShape shape) {
    this.shape = shape;
  }

  @Override
  public boolean isVisible() {
    return !(shape.getEnd().getParent() instanceof SimpleConnectionPointShape);
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
