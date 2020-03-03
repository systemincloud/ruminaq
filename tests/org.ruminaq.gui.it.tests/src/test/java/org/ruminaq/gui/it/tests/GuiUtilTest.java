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
  @MethodSource("projectionOnSection")
  public void testProjectionOnSection(Point a, Point b, Point p, Point pp) {
    Point ppActual = GuiUtil.projectionOnSection(a, b, p);
    assertEquals("X position match", pp.getX(), ppActual.getX());
    assertEquals("Y position match", pp.getY(), ppActual.getY());
  }

  @Parameters
  public static Stream<Arguments> projectionOnSection() {
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

}
