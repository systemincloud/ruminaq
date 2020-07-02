/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.gui.model.diagram.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.gui.model.diagram.impl.simpleconnection.ArrowDecorator;

/**
 * GraphicsAlgorithm factories.
 * 
 * @author Marek Jagielski
 */
public enum ConnectionDecoratorsFactory {
  INSTANCE;

  private final List<Factory<? extends ConnectionDecorator>> factories = Collections
      .singletonList(new ShapeFactory<>(SimpleConnectionShape.class,
          ArrowDecorator.class));

  /**
   * Flyweight Factory of ConnectionDecorators for PictogramElement.
   * 
   * @param shape key for retrieving Flyweight
   */
  public EList<ConnectionDecorator> getConnectionDecorators(
      PictogramElement shape) {
    return new BasicEList<>(factories.stream()
        .filter(p -> p.isForThisShape(shape)).map(p -> p.get(shape))
        .filter(ConnectionDecorator.class::isInstance)
        .map(ConnectionDecorator.class::cast).collect(Collectors.toList()));
  }
}
