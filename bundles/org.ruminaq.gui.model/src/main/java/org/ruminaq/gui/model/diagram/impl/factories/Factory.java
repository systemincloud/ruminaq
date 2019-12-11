package org.ruminaq.gui.model.diagram.impl.factories;

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Shape;

public interface Factory {

  boolean isForThisShape(Shape shape);

  GraphicsAlgorithm getGA(Shape shape);
}
