/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.ILayoutService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ruminaq.consts.Constants;
import org.ruminaq.util.StyleUtil;

public class GuiUtil {

  private static final IGaService gaService = Graphiti.getGaService();

  static final IPeService peService = Graphiti.getPeService();

  public static int DATA_WIDTH = 36;

  public static int DATA_HEIGHT = 50;

  public static int PARTICIPANT_BAND_HEIGHT = 20;

  public static final int SHAPE_PADDING = 6;

  public static final int TEXT_PADDING = 5;

  private static final String LINE_BREAK = "\n";

  public static class Size {
    private int width;
    private int height;

    public Size(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public int getWidth() {
      return this.width;
    }

    public int getHeight() {
      return this.height;
    }
  }

  /**
   * Check if label was moved
   *
   * @param label label shape
   * @param pe    pictogram element of labeled element
   * @return label was not moved
   */
  public static boolean isLabelInDefaultPosition(ContainerShape label,
      PictogramElement pe) {

    int shapeX = pe.getGraphicsAlgorithm().getX();
    int shapeY = pe.getGraphicsAlgorithm().getY();
    int shapeWidth = pe.getGraphicsAlgorithm().getWidth();
    int shapeHeight = pe.getGraphicsAlgorithm().getHeight();
    int textWidth = label.getGraphicsAlgorithm().getWidth();
    int currentLabelX = label.getGraphicsAlgorithm().getX();
    int currentLabelY = label.getGraphicsAlgorithm().getY();

    int newShapeX = shapeX - ((textWidth) / 2) + shapeWidth / 2;
    int newShapeY = shapeY + shapeHeight + 2;

    return almostEqual(currentLabelX, newShapeX, 4)
        && almostEqual(currentLabelY, newShapeY, 4);
  }

  public static boolean isLabel(Object o) {
    return o instanceof ContainerShape && Graphiti.getPeService()
        .getPropertyValue((ContainerShape) o, Constants.LABEL_PROPERTY) != null;
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

  public static void alignWithShape(AbstractText text,
      ContainerShape labelContainer, int width, int height, int shapeX,
      int shapeY, int preShapeX, int preShapeY) {
    final int textHeight = getLabelHeight(text);
    final int textWidth = getLabelWidth(text);

    int currentLabelX = labelContainer.getGraphicsAlgorithm().getX();
    int currentLabelY = labelContainer.getGraphicsAlgorithm().getY();

    int newShapeX = shapeX - ((textWidth + SHAPE_PADDING) / 2) + width / 2;
    int newShapeY = shapeY + height + 2;

    if (currentLabelX > 0 && preShapeX > 0) {
      newShapeX = currentLabelX + (shapeX - preShapeX);
      newShapeY = currentLabelY + (shapeY - preShapeY);
    }

    IGaService gaService = Graphiti.getGaService();

    gaService.setLocationAndSize(labelContainer.getGraphicsAlgorithm(),
        newShapeX, newShapeY, textWidth + SHAPE_PADDING,
        textHeight + SHAPE_PADDING);
    gaService.setLocationAndSize(text, 0, 0, textWidth + TEXT_PADDING,
        textHeight + TEXT_PADDING);
  }

  public static void onRightOfShape(AbstractText text,
      ContainerShape labelContainer, int width, int height, int shapeX,
      int shapeY, int preShapeX, int preShapeY) {
    final int textHeight = getLabelHeight(text);
    final int textWidth = getLabelWidth(text);

    text.setRotation(0.0);

    int currentLabelX = labelContainer.getGraphicsAlgorithm().getX();
    int currentLabelY = labelContainer.getGraphicsAlgorithm().getY();

    int newShapeX = shapeX + width;
    int newShapeY = shapeY - ((textHeight + 2) >> 1) + (height >> 1);

    if (currentLabelX > 0 && preShapeX > 0) {
      newShapeX = currentLabelX + (shapeX - preShapeX);
      newShapeY = currentLabelY + (shapeY - preShapeY);
    }

    IGaService gaService = Graphiti.getGaService();

    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);

    gaService.setLocationAndSize(labelContainer.getGraphicsAlgorithm(),
        newShapeX, newShapeY, textWidth + SHAPE_PADDING,
        textHeight + SHAPE_PADDING);
    gaService.setLocationAndSize(text, 0, 0, textWidth + TEXT_PADDING,
        textHeight + TEXT_PADDING);
  }

  public static void onLeftOfShape(AbstractText text,
      ContainerShape labelContainer, int width, int height, int shapeX,
      int shapeY, int preShapeX, int preShapeY) {
    final int textHeight = getLabelHeight(text);
    final int textWidth = getLabelWidth(text);

    text.setRotation(0.0);

    int newShapeX = shapeX - textWidth - TEXT_PADDING;
    int newShapeY = shapeY - ((textHeight + 2) >> 1) + (height >> 1);

    IGaService gaService = Graphiti.getGaService();

    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);

    gaService.setLocationAndSize(labelContainer.getGraphicsAlgorithm(),
        newShapeX, newShapeY, textWidth + SHAPE_PADDING,
        textHeight + SHAPE_PADDING);
    gaService.setLocationAndSize(text, 0, 0, textWidth + TEXT_PADDING,
        textHeight + TEXT_PADDING);
  }

  public static void onTopOfShape(AbstractText text,
      ContainerShape labelContainer, int width, int height, int shapeX,
      int shapeY, int preShapeX, int preShapeY) {
    final int textHeight = getLabelHeight(text);
    final int textWidth = getLabelWidth(text);

    text.setRotation(-90.0);

    int newShapeX = shapeX - ((textHeight + 2) >> 1) + (width >> 1);
    int newShapeY = shapeY - textWidth - TEXT_PADDING;

    IGaService gaService = Graphiti.getGaService();

    gaService.setLocationAndSize(labelContainer.getGraphicsAlgorithm(),
        newShapeX, newShapeY, textHeight + SHAPE_PADDING,
        textWidth + SHAPE_PADDING);
    gaService.setLocationAndSize(text, -(textWidth >> 1) + TEXT_PADDING, -3,
        textWidth + SHAPE_PADDING, textWidth + SHAPE_PADDING);
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
  }

  public static void onBottomOfShape(AbstractText text,
      ContainerShape labelContainer, int width, int height, int shapeX,
      int shapeY, int preShapeX, int preShapeY) {
    final int textHeight = getLabelHeight(text);
    final int textWidth = getLabelWidth(text);

    text.setRotation(90.0);

    int newShapeX = shapeX - ((textHeight) >> 1) + (width >> 1);
    int newShapeY = shapeY + height;

    IGaService gaService = Graphiti.getGaService();

    gaService.setLocationAndSize(labelContainer.getGraphicsAlgorithm(),
        newShapeX, newShapeY, textHeight, textWidth + SHAPE_PADDING);
    gaService.setLocationAndSize(text, -(textWidth >> 1) + TEXT_PADDING,
        TEXT_PADDING, textWidth + TEXT_PADDING, textWidth);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);

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

  public static Polygon createDataArrow(Polygon p) {
    int[] points = { 4, 8, 14, 8, 14, 4, 18, 10, 14, 16, 14, 12, 4, 12 };
    Polygon arrow = gaService.createPolygon(p, points);
    arrow.setLineWidth(1);
    return arrow;
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
    if (y2 + h2 < y1 || y1 + h1 < y2 || x2 + w2 < x1 || x1 + w1 < x2) {
      return false;
    }
    return true;
  }

  public static boolean intersectsLabel(ContainerShape label, Shape shape) {

    /*
     * _________________________\ X | / | A_______________D | | | | | | | | | |
     * |_____________| | B C \|/ * Y
     *
     * L - Label S - Shape
     */

    int xal, yal, xbl, ybl, xcl, ycl, xdl, ydl;
    int xas, yas, xbs, ybs, xcs, ycs, xds, yds;

    xal = label.getGraphicsAlgorithm().getX();
    yal = label.getGraphicsAlgorithm().getY();
    xbl = xal;
    ybl = yal + label.getGraphicsAlgorithm().getHeight();
    xdl = xal + label.getGraphicsAlgorithm().getWidth();
    ydl = yal;
    xcl = xdl;
    ycl = ybl;

    xas = shape.getGraphicsAlgorithm().getX();
    yas = shape.getGraphicsAlgorithm().getY();
    xbs = xas;
    ybs = yas + shape.getGraphicsAlgorithm().getHeight();
    xds = xas + shape.getGraphicsAlgorithm().getWidth();
    yds = yas;
    xcs = xds;
    ycs = ybs;

    if ((xcl > xas && ycl > yas) && (xcl < xcs && ycl < ycs))
      return true;
    if ((xdl > xbs && ydl < ybs) && (xdl < xds && ydl > yds))
      return true;
    if ((xal < xcs && yal < ycs) && (xal > xas && yal > yas))
      return true;
    if ((xbl < xds && ybl > yds) && (xbl > xbs && ybl < ybs))
      return true;

    return false;
  }

  public static boolean pointsEqual(Point p1, Point p2) {
    return p1.getX() == p2.getX() && p1.getY() == p2.getY();
  }

  public static Point createPoint(Point p) {
    return gaService.createPoint(p.getX(), p.getY());
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
    int x = p1.getX() + dx / 2;
    int y = p1.getY() + dy / 2;
    return createPoint(x, y);
  }

  public static double getLength(ILocation start, ILocation end) {
    double a = start.getX() - end.getX();
    double b = start.getY() - end.getY();
    return Math.sqrt(a * a + b * b);
  }

  public static double getLength(Point p1, Point p2) {
    double a = p1.getX() - p2.getX();
    double b = p1.getY() - p2.getY();
    return Math.sqrt(a * a + b * b);
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

  public static double distance(Point p1, Point p2) {
    return Math.sqrt((p1.getX() - p2.getX()) ^ 2 + (p1.getY() - p2.getY()) ^ 2);
  }

  public static int distanceToConnection(FreeFormConnection ffc, int x, int y,
      String internalPortProperty) {

    int x_start = ffc.getStart().getParent().getGraphicsAlgorithm().getX()
        + (ffc.getStart().getParent().getGraphicsAlgorithm().getWidth() >> 1);
    int y_start = ffc.getStart().getParent().getGraphicsAlgorithm().getY()
        + (ffc.getStart().getParent().getGraphicsAlgorithm().getHeight() >> 1);
    String isInternalPort = Graphiti.getPeService()
        .getPropertyValue(ffc.getStart().getParent(), internalPortProperty);
    if (Boolean.parseBoolean(isInternalPort)) {
      x_start = x_start + ((ContainerShape) ffc.getStart().getParent())
          .getContainer().getGraphicsAlgorithm().getX();
      y_start = y_start + ((ContainerShape) ffc.getStart().getParent())
          .getContainer().getGraphicsAlgorithm().getY();
    }
    int x_end = ffc.getEnd().getParent().getGraphicsAlgorithm().getX()
        + (ffc.getEnd().getParent().getGraphicsAlgorithm().getWidth() >> 1);
    int y_end = ffc.getEnd().getParent().getGraphicsAlgorithm().getY()
        + (ffc.getEnd().getParent().getGraphicsAlgorithm().getHeight() >> 1);
    isInternalPort = Graphiti.getPeService()
        .getPropertyValue(ffc.getEnd().getParent(), internalPortProperty);
    if (Boolean.parseBoolean(isInternalPort)) {
      x_end = x_end + ((ContainerShape) ffc.getEnd().getParent()).getContainer()
          .getGraphicsAlgorithm().getX();
      y_end = y_end + ((ContainerShape) ffc.getEnd().getParent()).getContainer()
          .getGraphicsAlgorithm().getY();
    }

    EList<Point> points = ffc.getBendpoints();

    int d = Integer.MAX_VALUE;

    if (points.size() == 0) {
      d = distanceToSection(x_start, y_start, x_end, y_end, x, y);
      return d;
    }

    for (int i = 0; i <= points.size(); i++) {
      int x_next = 0, y_next = 0, x_before = 0, y_before = 0;

      if (i == 0) {
        x_before = x_start;
        y_before = y_start;
        x_next = points.get(i).getX();
        y_next = points.get(i).getY();
      } else if (i > 0 && i < points.size()) {
        x_before = points.get(i - 1).getX();
        y_before = points.get(i - 1).getY();
        x_next = points.get(i).getX();
        y_next = points.get(i).getY();
      } else {
        x_before = points.get(i - 1).getX();
        y_before = points.get(i - 1).getY();
        x_next = x_end;
        y_next = y_end;
      }

      int d_tmp = distanceToSection(x_before, y_before, x_next, y_next, x, y);
      if (d_tmp < d)
        d = d_tmp;
    }

    return d;
  }

  public static int distanceToSection(int x1, int y1, int x2, int y2, int xp,
      int yp) {
    Point d = projectionOnSection(x1, y1, x2, y2, xp, yp);
    if (((Math.min(d.getX(), x1) <= x2) && (x2 <= Math.max(d.getX(), x1))
        && (Math.min(d.getY(), y1) <= y2) && (y2 <= Math.max(d.getY(), y1))))
      return Integer.MAX_VALUE;
    return (int) Math
        .sqrt(Math.pow(d.getX() - xp, 2) + Math.pow(d.getY() - yp, 2));
  }

  private static Point projectionOnSection(int x1, int y1, int x2, int y2,
      int xp, int yp) {
    int denominator = x1 * x1 - 2 * x1 * x2 + x2 * x2 + y1 * y1 - 2 * y1 * y2
        + y2 * y2;
    int d_x = Integer.MAX_VALUE;
    int d_y = Integer.MAX_VALUE;

    if (denominator == 0) {
      d_x = x1;
      d_y = yp;
    } else {
      d_x = (xp * x1 * x1 - 2 * xp * x1 * x2 - x1 * y1 * y2 + yp * x1 * y1
          + x1 * y2 * y2 - yp * x1 * y2 + xp * x2 * x2 + x2 * y1 * y1
          - x2 * y1 * y2 - yp * x2 * y1 + yp * x2 * y2) / denominator;
      d_y = (x1 * x1 * y2 - x1 * x2 * y1 - x1 * x2 * y2 + xp * x1 * y1
          - xp * x1 * y2 + x2 * x2 * y1 - xp * x2 * y1 + xp * x2 * y2
          + yp * y1 * y1 - 2 * yp * y1 * y2 + yp * y2 * y2) / denominator;
    }
    return createPoint(d_x, d_y);
  }

  public static boolean pointBelongsToSection(Point d, int x1, int y1, int x2,
      int y2) {
    int det = d.getX() * y1 + x1 * y2 + x2 * d.getY() - x2 * y1 - d.getX() * y2
        - x1 * d.getY();
    if (det != 0)
      return false;
    else if ((Math.min(d.getX(), x1) <= x2) && (x2 <= Math.max(d.getX(), x1))
        && (Math.min(d.getY(), y1) <= y2) && (y2 <= Math.max(d.getY(), y1)))
      return true;
    else
      return false;
  }

  public static boolean pointBelongsToSection(Point p, int x1, int y1, int x2,
      int y2, int epsilon) {
    int d = distanceToSection(x1, y1, x2, y2, p.getX(), p.getY());
    if (d <= epsilon)
      return true;
    else
      return false;
  }

  public static Point projectOnConnection(FreeFormConnection ffc, int x, int y,
      String internalPortProperty) {

    int x_start = ffc.getStart().getParent().getGraphicsAlgorithm().getX()
        + (ffc.getStart().getParent().getGraphicsAlgorithm().getWidth() >> 1);
    int y_start = ffc.getStart().getParent().getGraphicsAlgorithm().getY()
        + (ffc.getStart().getParent().getGraphicsAlgorithm().getHeight() >> 1);
    String isInternalPort = Graphiti.getPeService()
        .getPropertyValue(ffc.getStart().getParent(), internalPortProperty);
    if (Boolean.parseBoolean(isInternalPort)) {
      x_start = x_start + ((ContainerShape) ffc.getStart().getParent())
          .getContainer().getGraphicsAlgorithm().getX();
      y_start = y_start + ((ContainerShape) ffc.getStart().getParent())
          .getContainer().getGraphicsAlgorithm().getY();
    }
    int x_end = ffc.getEnd().getParent().getGraphicsAlgorithm().getX()
        + (ffc.getEnd().getParent().getGraphicsAlgorithm().getWidth() >> 1);
    int y_end = ffc.getEnd().getParent().getGraphicsAlgorithm().getY()
        + (ffc.getEnd().getParent().getGraphicsAlgorithm().getHeight() >> 1);
    isInternalPort = Graphiti.getPeService()
        .getPropertyValue(ffc.getEnd().getParent(), internalPortProperty);
    if (Boolean.parseBoolean(isInternalPort)) {
      x_end = x_end + ((ContainerShape) ffc.getEnd().getParent()).getContainer()
          .getGraphicsAlgorithm().getX();
      y_end = y_end + ((ContainerShape) ffc.getEnd().getParent()).getContainer()
          .getGraphicsAlgorithm().getY();
    }
    EList<Point> points = ffc.getBendpoints();

    if (points.size() == 0) {
      Point pd = projectionOnSection(x_start, y_start, x_end, y_end, x, y);
      if (((Math.min(pd.getX(), x_start) <= x_end)
          && (x_end <= Math.max(pd.getX(), x_start))
          && (Math.min(pd.getY(), y_start) <= y_end)
          && (y_end <= Math.max(pd.getY(), y_start))))
        return null;
      else
        return pd;
    }

    int d = Integer.MAX_VALUE;
    Point p = null;

    for (int i = 0; i <= points.size(); i++) {
      int x_next = 0, y_next = 0, x_before = 0, y_before = 0;

      if (i == 0) {
        x_before = x_start;
        y_before = y_start;
        x_next = points.get(i).getX();
        y_next = points.get(i).getY();
      } else if (i > 0 && i < points.size()) {
        x_before = points.get(i - 1).getX();
        y_before = points.get(i - 1).getY();
        x_next = points.get(i).getX();
        y_next = points.get(i).getY();
      } else {
        x_before = points.get(i - 1).getX();
        y_before = points.get(i - 1).getY();
        x_next = x_end;
        y_next = y_end;
      }

      Point pd = projectionOnSection(x_before, y_before, x_next, y_next, x, y);
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

  public static Polygon createArrow(GraphicsAlgorithmContainer gaContainer,
      Diagram diagram) {
    int xy[] = new int[] { -10, -5, 0, 0, -10, 5, -8, 0 };
    int beforeAfter[] = new int[] { 3, 3, 0, 0, 3, 3, 3, 3 };
    Polygon polyline = Graphiti.getGaCreateService().createPolygon(gaContainer,
        xy, beforeAfter);
    polyline.setStyle(StyleUtil.getStyleForPolygon(diagram));
    return polyline;
  }

}