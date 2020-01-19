/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.WeakHashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.slf4j.Logger;

/**
 * Factory for creating Label.
 *
 * @author Marek Jagielski
 */
public class ShapeFactory<T extends Shape, K extends GraphicsAlgorithm>
    implements Factory {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(ShapeFactory.class);
  
  private Class<T> shapeType;
  private Class<K> gaType;

  private WeakHashMap<EObject, K> cacheGraphicsAlgorithms = new WeakHashMap<>();

  public ShapeFactory(Class<T> shapeType, Class<K> gaType) {
    this.shapeType = shapeType;
    this.gaType = gaType;
  }

  @Override
  public boolean isForThisShape(Shape shape) {
    return shapeType.isInstance(shape);
  }

  @Override
  public GraphicsAlgorithm getGA(Shape shape) {
    if (!shapeType.isInstance(shape)) {
      return null;
    }

    if (!cacheGraphicsAlgorithms.containsKey(shape)) {
      K graphicsAlgorithm = null;
      try {
        graphicsAlgorithm = gaType.getConstructor(shapeType).newInstance(shape);
      } catch (InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException
          | NoSuchMethodException | SecurityException e) {
        LOGGER.error("", e);;
      }
      cacheGraphicsAlgorithms.put(shape, graphicsAlgorithm);
    }
    return cacheGraphicsAlgorithms.get(shape);
  }
}
