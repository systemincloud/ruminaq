package org.ruminaq.gui.it.tests;

import static org.junit.Assert.assertEquals;
import static org.ruminaq.gui.model.diagram.impl.GuiUtil.createPoint;

import java.util.stream.Stream;

import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.ruminaq.gui.model.diagram.impl.GuiUtil;

@RunWith(JUnitPlatform.class)
public class GuiUtilTest {

  @ParameterizedTest
  @MethodSource("projectionOnLine")
  public void testProjectionOnLine(Point a, Point b, Point p, Point pp) {
    Point ppActual = GuiUtil.projectionOnLine(a, b, p);
    assertEquals("X position match", pp.getX(), ppActual.getX());
    assertEquals("Y position match", pp.getY(), ppActual.getY());
  }

  @Parameters
  public static Stream<Arguments> projectionOnLine() {
    return Stream.of(
        Arguments.of(createPoint(0, 0), createPoint(10, 0), createPoint(5, 0),
            createPoint(5, 0)),
        Arguments.of(createPoint(0, 0), createPoint(10, 0), createPoint(5, 5),
            createPoint(5, 0)),
        Arguments.of(createPoint(0, 0), createPoint(0, 10), createPoint(0, 5),
            createPoint(0, 5)),
        Arguments.of(createPoint(0, 0), createPoint(0, 10), createPoint(5, 5),
            createPoint(0, 5)),
        Arguments.of(createPoint(20, 10), createPoint(30, 10),
            createPoint(25, 5), createPoint(25, 10)),
        Arguments.of(createPoint(20, 20), createPoint(30, 10),
            createPoint(30, 20), createPoint(25, 15)),
        Arguments.of(createPoint(100, 103), createPoint(104, 100),
            createPoint(102, 103), createPoint(101, 102)),
        Arguments.of(createPoint(100, 103), createPoint(104, 100),
            createPoint(102, 100), createPoint(102, 100)),
        Arguments.of(createPoint(100, 103), createPoint(104, 100),
            createPoint(111, 101), createPoint(108, 97)));
  }

  @ParameterizedTest
  @MethodSource("distanceToLine")
  public void testDistanceToLine(Point a, Point b, Point p, int d) {
    assertEquals("Distance match", d, GuiUtil.distanceToLine(a, b, p), 1);
  }

  @Parameters
  public static Stream<Arguments> distanceToLine() {
    return Stream.of(
        Arguments.of(createPoint(0, 0), createPoint(10, 0), createPoint(5, 0),
            0),
        Arguments.of(createPoint(0, 0), createPoint(10, 0), createPoint(5, 5),
            5),
        Arguments.of(createPoint(100, 103), createPoint(104, 100),
            createPoint(111, 101), 5));
  }
  
  @ParameterizedTest
  @MethodSource("distanceToSection")
  public void testDistanceToSection(Point a, Point b, Point p, int d) {
    assertEquals("Distance match", d, GuiUtil.distanceToSection(a, b, p), 1);
  }

  @Parameters
  public static Stream<Arguments> distanceToSection() {
    return Stream.of(
        Arguments.of(createPoint(0, 0), createPoint(10, 0), createPoint(5, 0),
            0),
        Arguments.of(createPoint(0, 0), createPoint(10, 0), createPoint(5, 5),
            5),
        Arguments.of(createPoint(0, 0), createPoint(0, 10), createPoint(0, 5),
            0),
        Arguments.of(createPoint(0, 0), createPoint(0, 10), createPoint(5, 5),
            5),
        Arguments.of(createPoint(20, 10), createPoint(30, 10),
            createPoint(25, 5), 5),
        Arguments.of(createPoint(20, 20), createPoint(30, 10),
            createPoint(30, 20), 7),
        Arguments.of(createPoint(100, 103), createPoint(104, 100),
            createPoint(102, 103), 1),
        Arguments.of(createPoint(100, 103), createPoint(104, 100),
            createPoint(102, 100), 0),
        Arguments.of(createPoint(100, 103), createPoint(104, 100),
            createPoint(111, 101), 7));
  }

  @ParameterizedTest
  @MethodSource("distanceBetweenPoints")
  public void testDistanceBetweenPoints(Point a, Point b, int d) {
    assertEquals("Distance match", d, GuiUtil.distanceBetweenPoints(a, b), 1);
  }

  @Parameters
  public static Stream<Arguments> distanceBetweenPoints() {
    return Stream.of(Arguments.of(createPoint(0, 0), createPoint(10, 0), 10),
        Arguments.of(createPoint(0, 0), createPoint(10, 0), 10),
        Arguments.of(createPoint(20, 10), createPoint(30, 10), 10),
        Arguments.of(createPoint(100, 103), createPoint(104, 100), 5));
  }

  @ParameterizedTest
  @MethodSource("pointBelongsToLine")
  public void testPointBelongsToLine(Point a, Point b, Point p,
      boolean result) {
    assertEquals("Point match", result, GuiUtil.pointBelongsToLine(a, b, p));
  }

  @Parameters
  public static Stream<Arguments> pointBelongsToLine() {
    return Stream.of(
        Arguments.of(createPoint(0, 0), createPoint(10, 0), createPoint(5, 0),
            true),
        Arguments.of(createPoint(0, 0), createPoint(0, 10), createPoint(0, 5),
            true),
        Arguments.of(createPoint(0, 10), createPoint(10, 0), createPoint(5, 5),
            true),
        Arguments.of(createPoint(0, 10), createPoint(10, 0), createPoint(6, 6),
            true),
        Arguments.of(createPoint(0, 10), createPoint(10, 0), createPoint(7, 7),
            false),
        Arguments.of(createPoint(0, 10), createPoint(10, 0), createPoint(6, 5),
            true),
        Arguments.of(createPoint(100, 103), createPoint(104, 100), createPoint(103, 101),
            true));
  }
  
  @ParameterizedTest
  @MethodSource("pointBelongsToSection")
  public void testPointBelongsToSection(Point a, Point b, Point p,
      boolean result) {
    assertEquals("Point match", result, GuiUtil.pointBelongsToSection(a, b, p));
  }

  @Parameters
  public static Stream<Arguments> pointBelongsToSection() {
    return Stream.of(
        Arguments.of(createPoint(0, 0), createPoint(10, 0), createPoint(5, 0),
            true),
        Arguments.of(createPoint(0, 0), createPoint(10, 0), createPoint(12, 0),
            false),
        Arguments.of(createPoint(0, 0), createPoint(0, 10), createPoint(0, 5),
            true),
        Arguments.of(createPoint(0, 0), createPoint(0, 10), createPoint(0, 12),
            false),
        Arguments.of(createPoint(0, 10), createPoint(10, 0), createPoint(5, 5),
            true),
        Arguments.of(createPoint(0, 10), createPoint(10, 0), createPoint(6, 6),
            true),
        Arguments.of(createPoint(0, 10), createPoint(10, 0), createPoint(7, 7),
            false),
        Arguments.of(createPoint(0, 10), createPoint(10, 0), createPoint(6, 5),
            true),
        Arguments.of(createPoint(100, 103), createPoint(104, 100), createPoint(103, 101),
            true));
  }
}
