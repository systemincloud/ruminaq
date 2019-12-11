package org.ruminaq.gui.model.diagram.impl;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.model.diagram.impl.factories.PortShapeFactory;
import org.ruminaq.gui.model.diagram.impl.factories.Factory;

public enum GraphicsAlgorithmFactory {
  INSTANCE;

  private final List<Factory> factories = Arrays
      .asList(PortShapeFactory.INSTANCE);

  public GraphicsAlgorithm getGraphicsAlgorithm(Shape shape) {
    return factories.stream().filter((Factory p) -> p.isForThisShape(shape))
        .findFirst().map((Factory p) -> p.getGA(shape)).orElseThrow();
  }
}
