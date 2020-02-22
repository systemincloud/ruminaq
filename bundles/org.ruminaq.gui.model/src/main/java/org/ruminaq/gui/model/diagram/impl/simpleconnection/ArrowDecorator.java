/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.simpleconnection;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.impl.PolygonImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.impl.ConnectionDecoratorImpl;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;

/**
 * Arrow of SimpleConnection.
 *
 * @author Marek Jagielski
 */
public class ArrowDecorator extends ConnectionDecoratorImpl {

  private static final int[] XY = new int[] { -10, -5, 0, 0, -10, 5, -8, 0 };

  private static final int[] BEFORE_AFTER = new int[] { 3, 3, 0, 0, 3, 3, 3,
      3 };

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

  private GraphicsAlgorithm ga = new Arrow();

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
