/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.gui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.ILayoutService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ruminaq.gui.model.diagram.RuminaqShape;

/**
 * Gui utils methods.
 *
 * @author Marek Jagielski
 */
public final class GuiUtil {

  private static final IGaService gaService = Graphiti.getGaService();

  private static final IPeService peService = Graphiti.getPeService();

  private static final int SQUARE = 2;

  private static final double ERROR_MARGIN = 1.5;

  private static final String LINE_BREAK = "\n";

  private GuiUtil() {
    // Util class
  }

  /**
   * Euclidean distance to line determined by two points.
   *
   * @param a Point on line
   * @param b Point on line
   * @param p Any point
   * @return distance to line
   */
  public static double distanceToLine(Point a, Point b, Point p) {
    return distanceBetweenPoints(projectionOnLine(a, b, p), p);
  }

  /**
   * Euclidean distance to section.
   *
   * @param a Point of the section
   * @param b Point of the section
   * @param p Any point
   * @return distance to section
   */
  public static double distanceToSection(Point a, Point b, Point p) {
    return Optional.of(projectionOnLine(a, b, p))
        .filter(pp -> pointBelongsToSection(a, b, pp))
        .map(pp -> distanceBetweenPoints(pp, p))
        .orElseGet(() -> Stream.of(a, b).map(pi -> distanceBetweenPoints(pi, p))
            .min(Double::compareTo).orElse(Double.MAX_VALUE));
  }

  /**
   * Euclidean distance between points.
   *
   * @param a Point of the section
   * @param b Point of the section
   * @return distance between points
   */
  public static double distanceBetweenPoints(Point a, Point b) {
    return Math.sqrt(Math.pow((double) (a.getX() - b.getX()), SQUARE)
        + Math.pow((double) (a.getY() - b.getY()), SQUARE));
  }

  /**
   * Projection of Point on line determined by two points.
   *
   * @param a Point of the line
   * @param b Point of the line
   * @param p Any point
   * @return point being projections of Point p on lines |ab|
   */
  public static Point projectionOnLine(Point a, Point b, Point p) {
    int denominator = a.getX() * a.getX() - ((a.getX() * b.getX()) << 1)
        + b.getX() * b.getX() + a.getY() * a.getY()
        - ((a.getY() * b.getY()) << 1) + b.getY() * b.getY();
    int xPrim;
    int yPrim;

    if (denominator == 0) {
      xPrim = a.getX();
      yPrim = p.getY();
    } else {
      xPrim = (p.getX() * a.getX() * a.getX()
          - ((p.getX() * a.getX() * b.getX()) << 1)
          - a.getX() * a.getY() * b.getY() + p.getY() * a.getX() * a.getY()
          + a.getX() * b.getY() * b.getY() - p.getY() * a.getX() * b.getY()
          + p.getX() * b.getX() * b.getX() + b.getX() * a.getY() * a.getY()
          - b.getX() * a.getY() * b.getY() - p.getY() * b.getX() * a.getY()
          + p.getY() * b.getX() * b.getY()) / denominator;
      yPrim = (a.getX() * a.getX() * b.getY() - a.getX() * b.getX() * a.getY()
          - a.getX() * b.getX() * b.getY() + p.getX() * a.getX() * a.getY()
          - p.getX() * a.getX() * b.getY() + b.getX() * b.getX() * a.getY()
          - p.getX() * b.getX() * a.getY() + p.getX() * b.getX() * b.getY()
          + p.getY() * a.getY() * a.getY()
          - ((p.getY() * a.getY() * b.getY()) << 1)
          + p.getY() * b.getY() * b.getY()) / denominator;
    }

    return createPoint(xPrim, yPrim);
  }

  /**
   * Check if point lays in line with other two points. The distance should be
   * less than 1.
   *
   * @param a one point that lays on line
   * @param b second point that lays on line
   * @param p point to check if also lays on that line
   * @return true if point p is in line with points a and b
   */
  public static boolean pointBelongsToLine(Point a, Point b, Point p) {
    return pointBelongsToLine(a, b, p, ERROR_MARGIN);
  }

  /**
   * Check if point lays in line with other two points.
   *
   * @param a       one point that lays on line
   * @param b       second point that lays on line
   * @param p       point to check if also lays on that line
   * @param epsilon distance to line that is still considered as point belongs
   *                to line
   * @return true if point p is in line with points a and b
   */
  public static boolean pointBelongsToLine(Point a, Point b, Point p,
      double epsilon) {
    return distanceToLine(a, b, p) <= epsilon;
  }

  /**
   * Check if point lays on line and is between two points.
   *
   * @param a edge point of section
   * @param b edge point of section
   * @param p any point
   * @return true if point lays on section
   */
  public static boolean pointBelongsToSection(Point a, Point b, Point p) {
    return pointBelongsToLine(a, b, p)
        && whenAisGreaterThenB(a.getX(), b.getX(), p.getX())
        && whenAisLowerThenB(a.getX(), b.getX(), p.getX())
        && whenAisGreaterThenB(a.getY(), b.getY(), p.getY())
        && whenAisLowerThenB(a.getY(), b.getY(), p.getY());
  }

  private static boolean whenAisGreaterThenB(int a, int b, int p) {
    return a < b || (p >= b && p <= a);
  }

  private static boolean whenAisLowerThenB(int a, int b, int p) {
    return a > b || (p <= b && p >= a);
  }

  // TODO: Think about line break in the ui...
  public static int getLabelHeight(AbstractText text) {
    if (text.getValue() != null && !text.getValue().isEmpty()) {
      String[] strings = text.getValue().split(LINE_BREAK);
      return strings.length * 14;
    }
    return 0;
  }

  // TODO: Think about a maximum-width...
  public static int getLabelWidth(AbstractText text) {
    if (text.getValue() != null && !text.getValue().isEmpty()) {
      String[] strings = text.getValue().split(LINE_BREAK);
      int result = 0;
      for (String string : strings) {
        IDimension dim = GraphitiUi.getUiLayoutService()
            .calculateTextSize(string, text.getFont());
        if (dim.getWidth() > result) {
          result = dim.getWidth();
        }
      }
      return result;
    }
    return 0;
  }

  public static boolean almostEqual(int a, int b, int eps) {
    return Math.abs(a - b) < eps;
  }

  public static boolean almostEqualRight(int a, int b, int eps) {
    return a - b < eps && a - b >= 0;
  }

  private static int getShapeHeight(Shape shape) {
    return shape.getGraphicsAlgorithm().getHeight();
  }

  private static int getShapeWidth(Shape shape) {
    return shape.getGraphicsAlgorithm().getWidth();
  }

  public static Shape getContainedShape(ContainerShape container,
      String propertyKey) {
    IPeService peService = Graphiti.getPeService();
    Iterator<Shape> iterator = peService.getAllContainedShapes(container)
        .iterator();
    while (iterator.hasNext()) {
      Shape shape = iterator.next();
      String property = peService.getPropertyValue(shape, propertyKey);
      if (property != null && Boolean.parseBoolean(property)) {
        return shape;
      }
    }
    return null;
  }

  public static List<PictogramElement> getContainedPictogramElements(
      PictogramElement container, String propertyKey) {
    List<PictogramElement> pictogramElements = new ArrayList<>();
    IPeService peService = Graphiti.getPeService();
    Iterator<PictogramElement> iterator = peService
        .getAllContainedPictogramElements(container).iterator();
    while (iterator.hasNext()) {
      PictogramElement pe = iterator.next();
      String property = peService.getPropertyValue(pe, propertyKey);
      if (property != null && Boolean.parseBoolean(property)) {
        pictogramElements.add(pe);
      }
    }
    return pictogramElements;
  }

  /**
   * Check if the given Point is with a given distance of the given Location.
   *
   * @param p    - the Point to check
   * @param loc  - the target Location
   * @param dist - the maximum distance horizontally and vertically from the
   *             given Location
   * @return true if the point lies within the rectangular area of the Location.
   */
  public static boolean isPointNear(Point p, ILocation loc, int dist) {
    int x = p.getX();
    int y = p.getY();
    int lx = loc.getX();
    int ly = loc.getY();
    return lx - dist <= x && x <= lx + dist && ly - dist <= y && y <= ly + dist;
  }

  public static boolean contains(Shape shape, Point point) {
    IDimension size = calculateSize(shape);
    ILocation loc = Graphiti.getLayoutService()
        .getLocationRelativeToDiagram(shape);
    int x = point.getX();
    int y = point.getY();
    return x >= loc.getX() && x <= loc.getX() + size.getWidth()
        && y >= loc.getY() && y < loc.getY() + size.getHeight();
  }

  public static boolean intersects(Shape shape1, Shape shape2) {
    ILayoutService layoutService = Graphiti.getLayoutService();
    ILocation loc2 = layoutService.getLocationRelativeToDiagram(shape2);
    int x2 = loc2.getX();
    int y2 = loc2.getY();
    int w2 = getShapeWidth(shape2);
    int h2 = getShapeHeight(shape2);
    return intersects(shape1, x2, y2, w2, h2);
  }

  public static boolean intersects(Shape shape1, int x2, int y2, int w2,
      int h2) {
    ILayoutService layoutService = Graphiti.getLayoutService();
    ILocation loc1 = layoutService.getLocationRelativeToDiagram(shape1);
    int x1 = loc1.getX();
    int y1 = loc1.getY();
    int w1 = getShapeWidth(shape1);
    int h1 = getShapeHeight(shape1);
    return intersects(x1, y1, w1, h1, x2, y2, w2, h2);
  }

  public static boolean intersects(int x1, int y1, int w1, int h1, int x2,
      int y2, int w2, int h2) {
    if (x2 <= x1 || y1 <= y2) {
      int t1, t2, t3, t4;
      t1 = x1;
      x1 = x2;
      x2 = t1;
      t2 = y1;
      y1 = y2;
      y2 = t2;
      t3 = w1;
      w1 = w2;
      w2 = t3;
      t4 = h1;
      h1 = h2;
      h2 = t4;
    }
    return !(y2 + h2 < y1 || y1 + h1 < y2 || x2 + w2 < x1 || x1 + w1 < x2);
  }

  public static boolean intersects(RuminaqShape label, RuminaqShape shape) {

    int xal = label.getX();
    int yal = label.getY();
    int xbl = xal;
    int ybl = yal + label.getHeight();
    int xdl = xal + label.getWidth();
    int ydl = yal;
    int xcl = xdl;
    int ycl = ybl;

    int xas = shape.getX();
    int yas = shape.getY();
    int xbs = xas;
    int ybs = yas + shape.getHeight();
    int xds = xas + shape.getWidth();
    int yds = yas;
    int xcs = xds;
    int ycs = ybs;

    return (xcl > xas && ycl > yas && xcl < xcs && ycl < ycs)
        || (xdl > xbs && ydl < ybs && xdl < xds && ydl > yds)
        || (xal < xcs && yal < ycs && xal > xas && yal > yas)
        || (xbl < xds && ybl > yds && xbl > xbs && ybl < ybs);
  }

  public static boolean pointsEqual(Point p1, Point p2) {
    return p1.getX() == p2.getX() && p1.getY() == p2.getY();
  }

  public static Point createPoint(int x, int y) {
    return gaService.createPoint(x, y);
  }

  public static Point createPoint(Anchor a) {
    return createPoint(peService.getLocationRelativeToDiagram(a));
  }

  public static Point createPoint(AnchorContainer ac) {
    if (ac instanceof Shape)
      return createPoint(peService.getLocationRelativeToDiagram((Shape) ac));
    return null;
  }

  public static Point createPoint(ILocation loc) {
    return createPoint(loc.getX(), loc.getY());
  }

  public static Point getMidpoint(Point p1, Point p2) {
    int dx = p2.getX() - p1.getX();
    int dy = p2.getY() - p1.getY();
    int x = p1.getX() + dx >> 1;
    int y = p1.getY() + dy >> 1;
    return createPoint(x, y);
  }

  public static IDimension calculateSize(AnchorContainer shape) {
    GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
    if (ga != null)
      return gaService.calculateSize(ga);

    IDimension dim = null;
    if (shape instanceof ContainerShape) {
      ContainerShape cs = (ContainerShape) shape;
      for (Shape s : cs.getChildren()) {
        ga = s.getGraphicsAlgorithm();
        if (ga != null) {
          IDimension d = gaService.calculateSize(ga);
          if (dim == null)
            dim = d;
          else {
            if (d.getWidth() > dim.getWidth())
              dim.setWidth(d.getWidth());
            if (d.getHeight() > dim.getHeight())
              dim.setHeight(d.getHeight());
          }
        }
      }
    }
    return dim;
  }

}
