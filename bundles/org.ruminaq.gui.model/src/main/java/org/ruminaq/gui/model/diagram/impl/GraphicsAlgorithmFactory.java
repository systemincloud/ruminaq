/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.model.diagram.impl.label.LabelShapeFactory;
import org.ruminaq.gui.model.diagram.impl.port.PortShapeFactory;

/**
 * GraphicsAlgorithm factories.
 * 
 * @author Marek Jagielski
 */
public enum GraphicsAlgorithmFactory {
  INSTANCE;

  private final List<Factory> factories = Arrays
      .asList(LabelShapeFactory.INSTANCE, PortShapeFactory.INSTANCE);

  public GraphicsAlgorithm getGraphicsAlgorithm(Shape shape) {
    return factories.stream().filter((Factory p) -> p.isForThisShape(shape))
        .findFirst().map((Factory p) -> p.getGA(shape)).orElseThrow();
  }
}
