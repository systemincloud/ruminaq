/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.simpleconnection;

import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.ruminaq.gui.model.GuiUtil;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;

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
        .map(me -> new SimpleEntry<SimpleEntry<Point, Point>, Point>(me,
            GuiUtil.projectionOnLine(me.getKey(), me.getValue(), point)))
        .filter(me -> GuiUtil.pointBelongsToSection(me.getKey().getKey(),
            me.getKey().getValue(), me.getValue()))
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
  public static Optional<Point> projectOnConnection(FreeFormConnection scs,
      int x, int y) {
    Point point = GuiUtil.createPoint(x, y);

    List<Point> points = getBendpointsWithEndings(scs);

    return IntStream.range(0, points.size() - 1)
        .mapToObj(i -> new SimpleEntry<Point, Point>(points.get(i),
            points.get(i + 1)))
        .map(me -> new SimpleEntry<SimpleEntry<Point, Point>, Point>(me,
            GuiUtil.projectionOnLine(me.getKey(), me.getValue(), point)))
        .filter(me -> GuiUtil.pointBelongsToSection(me.getKey().getKey(),
            me.getKey().getValue(), me.getValue()))
        .map(SimpleEntry::getValue)
        .map(pp -> new SimpleEntry<Point, Double>(pp,
            GuiUtil.distanceBetweenPoints(pp, point)))
        .min(Comparator.comparing(SimpleEntry::getValue))
        .map(SimpleEntry::getKey);
  }

  /**
   * List of bendpoints with starting and ending points.
   *
   * @param ffc connection shape
   * @return list of Points
   */
  public static List<Point> getBendpointsWithEndings(
      FreeFormConnection ffc) {
    Point startPoint = StylesFactory.eINSTANCE.createPoint();
    Optional<RuminaqShape> start = Optional.of(ffc.getStart().getParent())
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast);
    start.map(SimpleConnectionUtil::xOnDiagram).ifPresent(startPoint::setX);
    start.map(SimpleConnectionUtil::yOnDiagram).ifPresent(startPoint::setY);

    Point endPoint = StylesFactory.eINSTANCE.createPoint();
    Optional<RuminaqShape> end = Optional.of(ffc.getEnd().getParent())
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast);
    end.map(SimpleConnectionUtil::xOnDiagram).ifPresent(endPoint::setX);
    end.map(SimpleConnectionUtil::yOnDiagram).ifPresent(endPoint::setY);

    LinkedList<Point> points = new LinkedList<>(ffc.getBendpoints());
    points.add(0, startPoint);
    points.add(endPoint);

    return points;
  }

  private static int xOnDiagram(RuminaqShape ruminaqShape) {
    int xTmp = ruminaqShape.getX() + (ruminaqShape.getWidth() >> 1);
    if (ruminaqShape.getContainer() instanceof RuminaqDiagram) {
      return xTmp;
    } else if (ruminaqShape instanceof InternalPortShape) {
      return xOnDiagram(((InternalPortShape) ruminaqShape).getTask());
    } else {
      return xTmp + xOnDiagram(ruminaqShape.getParent());
    }
  }

  private static int yOnDiagram(RuminaqShape ruminaqShape) {
    int yTmp = ruminaqShape.getY() + (ruminaqShape.getHeight() >> 1);
    if (ruminaqShape.getContainer() instanceof RuminaqDiagram) {
      return yTmp;
    } else if (ruminaqShape instanceof InternalPortShape) {
      return yOnDiagram(((InternalPortShape) ruminaqShape).getTask());
    } else {
      return yTmp + yOnDiagram(ruminaqShape.getParent());
    }
  }

}
