/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.simpleconnection;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.IntStream;

import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.gui.model.diagram.impl.GuiUtil;

/**
 * SimpleConnection util class.
 *
 * @author Marek Jagielski
 */
public class SimpleConnectionUtil {

  private SimpleConnectionUtil() {
    // Util class
  }

  public static int distanceToConnection(SimpleConnectionShape scs, int x,
      int y) {

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

    ArrayList<Point> points = new ArrayList<Point>(scs.getBendpoints());
    points.add(0, startPoint);
    points.add(endPoint);

    return IntStream.range(0, points.size() - 1)
        .mapToObj(i -> new SimpleEntry<Point, Point>(points.get(i),
            points.get(i + 1)))
        .map(me -> GuiUtil.distanceToSection(me.getKey().getX(),
            me.getKey().getY(), me.getValue().getX(), me.getValue().getY(), x,
            y))
        .min(Integer::compareTo).orElse(Integer.MAX_VALUE);
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
