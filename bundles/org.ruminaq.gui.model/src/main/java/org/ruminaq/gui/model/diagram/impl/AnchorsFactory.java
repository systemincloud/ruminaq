/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.model.diagram.InputPortShape;
import org.ruminaq.gui.model.diagram.OutputPortShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.impl.port.InputPortAnchor;
import org.ruminaq.gui.model.diagram.impl.port.OutputPortAnchor;
import org.ruminaq.gui.model.diagram.impl.simpleconnectionpoint.SimpleConnectionPointAnchor;

/**
 * Anchor factories.
 * 
 * @author Marek Jagielski
 */
public enum AnchorsFactory {
  INSTANCE;

  private final List<Factory<? extends Anchor>> factories = Arrays.asList(
      new ShapeFactory<>(InputPortShape.class, InputPortAnchor.class),
      new ShapeFactory<>(OutputPortShape.class, OutputPortAnchor.class),
      new ShapeFactory<>(SimpleConnectionPointShape.class,
          SimpleConnectionPointAnchor.class));

  /**
   * Flyweight Factory of ConnectionDecorators for PictogramElement.
   * 
   * @param shape key for retrieving Flyweight
   */
  public EList<Anchor> getAnchors(PictogramElement shape) {
    return new BasicEList<>(
        factories.stream().filter(p -> p.isForThisShape(shape))
            .map(p -> p.get(shape)).filter(Anchor.class::isInstance)
            .map(Anchor.class::cast).collect(Collectors.toList()));
  }
}
