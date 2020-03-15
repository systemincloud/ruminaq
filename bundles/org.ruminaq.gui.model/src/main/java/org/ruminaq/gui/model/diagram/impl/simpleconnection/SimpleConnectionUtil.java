/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.simpleconnection;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.impl.GuiUtil;

/**
 * SimpleConnection util class.
 *
 * @author Marek Jagielski
 */
public final class SimpleConnectionUtil {

  private SimpleConnectionUtil() {
    // Util class
  }

  /**
   * Euclidean distance of point to connection.
   * 
   * @param scs FreeFormConnection that is on RuminaqDiagram
   * @param x   point's coordinate
   * @param y   point's coordinate
   * @return euclidean distance
   */
  public static double distanceToConnection(FreeFormConnection scs, int x,
      int y) {
    Point point = GuiUtil.createPoint(x, y);

    List<Point> points = getBendpointsWithEndings(scs);

    return IntStream.range(0, points.size() - 1)
        .mapToObj(i -> new SimpleEntry<Point, Point>(points.get(i),
            points.get(i + 1)))
        .map(me -> new SimpleEntry<SimpleEntry<Point, Point>, Point>(me, GuiUtil.projectionOnLine(me.getKey(), me.getValue(),
            point)))
        .filter(me -> GuiUtil.pointBelongsToSection(me.getKey().getKey(), me.getKey().getValue(),
            me.getValue()))
        .map(me -> GuiUtil.distanceBetweenPoints(me.getValue(), point))
        .min(Double::compareTo).orElse(Double.MAX_VALUE);
  }

  /**
   * Euclidean projection of point on connection.
   * 
   * @param scs FreeFormConnection that is on RuminaqDiagram
   * @param x   point's coordinate
   * @param y   point's coordinate
   * @return euclidean distance
   */
  public static Point projectOnConnection(FreeFormConnection scs, int x,
      int y) {
    Point point = GuiUtil.createPoint(x, y);

    List<Point> points = getBendpointsWithEndings(scs);

    int d = Integer.MAX_VALUE;
    Point p = null;

    for (int i = 0; i < points.size() - 1; i++) {
      int x_next = 0;
      int y_next = 0;
      int x_before = 0;
      int y_before = 0;

      x_before = points.get(i).getX();
      y_before = points.get(i).getY();
      x_next = points.get(i + 1).getX();
      y_next = points.get(i + 1).getY();

      Point pd = GuiUtil.projectionOnLine(points.get(i), points.get(i + 1),
          point);
      if (((Math.min(pd.getX(), x_before) <= x_next)
          && (x_next <= Math.max(pd.getX(), x_before))
          && (Math.min(pd.getY(), y_before) <= y_next)
          && (y_next <= Math.max(pd.getY(), y_before))))
        continue;
      int d_tmp = (int) Math
          .sqrt(Math.pow(pd.getX() - x, 2) + Math.pow(pd.getY() - y, 2));
      if (d_tmp < d) {
        d = d_tmp;
        p = pd;
      }
    }

    return p;
  }

  private static List<Point> getBendpointsWithEndings(FreeFormConnection scs) {
    Point startPoint = StylesFactory.eINSTANCE.createPoint();
    Optional<RuminaqShape> start = Optional.of(scs.getStart().getParent())
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast);
    start.map(SimpleConnectionUtil::xOnDiagram).ifPresent(startPoint::setX);
    start.map(SimpleConnectionUtil::yOnDiagram).ifPresent(startPoint::setY);

    Point endPoint = StylesFactory.eINSTANCE.createPoint();
    Optional<RuminaqShape> end = Optional.of(scs.getEnd().getParent())
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast);
    end.map(SimpleConnectionUtil::xOnDiagram).ifPresent(endPoint::setX);
    end.map(SimpleConnectionUtil::yOnDiagram).ifPresent(endPoint::setY);

    ArrayList<Point> points = new ArrayList<>(scs.getBendpoints());
    points.add(0, startPoint);
    points.add(endPoint);

    return points;
  }

  private static int xOnDiagram(RuminaqShape ruminaqShape) {
    int xTmp = ruminaqShape.getX() + (ruminaqShape.getWidth() >> 1);
    if (ruminaqShape.getContainer() instanceof RuminaqDiagram) {
      return xTmp;
    } else {
      return xTmp + xOnDiagram(ruminaqShape.getParent());
    }
  }

  private static int yOnDiagram(RuminaqShape ruminaqShape) {
    int yTmp = ruminaqShape.getY() + (ruminaqShape.getHeight() >> 1);
    if (ruminaqShape.getContainer() instanceof RuminaqDiagram) {
      return yTmp;
    } else {
      return yTmp + xOnDiagram(ruminaqShape.getParent());
    }
  }

}
