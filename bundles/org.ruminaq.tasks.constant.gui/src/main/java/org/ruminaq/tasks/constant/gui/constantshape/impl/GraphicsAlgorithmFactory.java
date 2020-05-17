/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.gui.constantshape.impl;

import java.util.Collections;
import java.util.List;

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.model.diagram.impl.Factory;
import org.ruminaq.gui.model.diagram.impl.ShapeFactory;
import org.ruminaq.runner.RunnerException;
import org.ruminaq.tasks.constant.gui.ConstantShapeGA;
import org.ruminaq.tasks.constant.gui.constantshape.ConstantShape;

/**
 * GraphicsAlgorithm flyweight factories.
 * 
 * @author Marek Jagielski
 */
public enum GraphicsAlgorithmFactory {
  INSTANCE;

  private final List<Factory<? extends GraphicsAlgorithm>> factories = Collections
      .singletonList(
          new ShapeFactory<>(ConstantShape.class, ConstantShapeGA.class));

  /**
   * Flyweight Factory of GraphicsAlgorithms for PictogramElement.
   * 
   * @param pe key for retrieving Flyweight
   */
  public GraphicsAlgorithm getGraphicsAlgorithm(PictogramElement pe) {
    return factories.stream().filter(p -> p.isForThisShape(pe)).findFirst()
        .map(p -> p.get(pe)).filter(GraphicsAlgorithm.class::isInstance)
        .map(GraphicsAlgorithm.class::cast)
        .orElseThrow(() -> new RunnerException("No GraphicsAlgorithm found."));
  }
}
