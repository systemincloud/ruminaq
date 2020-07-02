/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.gui.model.diagram.impl;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.model.diagram.InputPortShape;
import org.ruminaq.gui.model.diagram.InternalInputPortShape;
import org.ruminaq.gui.model.diagram.InternalOutputPortShape;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.OutputPortShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.gui.model.diagram.impl.label.LabelShapeGA;
import org.ruminaq.gui.model.diagram.impl.port.InputPortShapeGA;
import org.ruminaq.gui.model.diagram.impl.port.OutputPortShapeGA;
import org.ruminaq.gui.model.diagram.impl.simpleconnection.SimpleConnectionShapeGA;
import org.ruminaq.gui.model.diagram.impl.simpleconnectionpoint.SimpleConnectionPointShapeGA;
import org.ruminaq.gui.model.diagram.impl.task.InternalInputPortShapeGA;
import org.ruminaq.gui.model.diagram.impl.task.InternalOutputPortShapeGA;
import org.ruminaq.gui.model.diagram.impl.task.TaskShapeGA;

/**
 * GraphicsAlgorithm flyweight factories.
 * 
 * @author Marek Jagielski
 */
public enum GraphicsAlgorithmFactory {
  INSTANCE;

  private final List<Factory<? extends GraphicsAlgorithm>> factories = Arrays
      .asList(new ShapeFactory<>(LabelShape.class, LabelShapeGA.class),
          new ShapeFactory<>(InputPortShape.class, InputPortShapeGA.class),
          new ShapeFactory<>(OutputPortShape.class, OutputPortShapeGA.class),
          new ShapeFactory<>(SimpleConnectionShape.class,
              SimpleConnectionShapeGA.class),
          new ShapeFactory<>(SimpleConnectionPointShape.class,
              SimpleConnectionPointShapeGA.class),
          new ShapeFactory<>(
              TaskShape.class, TaskShapeGA.class),
          new ShapeFactory<>(InternalOutputPortShape.class,
              InternalOutputPortShapeGA.class),
          new ShapeFactory<>(InternalInputPortShape.class,
              InternalInputPortShapeGA.class));

  /**
   * Flyweight Factory of GraphicsAlgorithms for PictogramElement.
   * 
   * @param pe key for retrieving Flyweight
   */
  public GraphicsAlgorithm getGraphicsAlgorithm(PictogramElement pe) {
    return factories.stream().filter(p -> p.isForThisShape(pe)).findFirst()
        .map(p -> p.get(pe)).filter(GraphicsAlgorithm.class::isInstance)
        .map(GraphicsAlgorithm.class::cast).orElseThrow();
  }
}
