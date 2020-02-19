/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.slf4j.Logger;

/**
 * Generic factory creating K for RuminaqShape. Particular K should have a
 * constructor with just particular T as parameter.
 *
 * @author Marek Jagielski
 */
public class ShapeFactory<T extends PictogramElement, K> implements Factory<K> {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(ShapeFactory.class);

  protected Class<T> shapeType;
  protected Class<K> returnType;

  private WeakHashMap<EObject, K> cacheReturnObjects = new WeakHashMap<>();

  public ShapeFactory(Class<T> shapeType, Class<K> returnType) {
    this.shapeType = shapeType;
    this.returnType = returnType;
  }

  @Override
  public boolean isForThisShape(PictogramElement shape) {
    return shapeType.isInstance(shape);
  }

  @Override
  public K get(PictogramElement shape) {
    if (!shapeType.isInstance(shape)) {
      return null;
    }

    if (!cacheReturnObjects.containsKey(shape)) {
      K returnObject = null;
      try {

        Optional<Constructor<?>> constructor = Stream
            .of(returnType.getConstructors())
            .filter(c -> c.getParameterTypes().length == 1
                && c.getParameterTypes()[0] == shapeType)
            .findFirst();
        if (constructor.isPresent()) {
          returnObject = returnType.getConstructor(shapeType)
              .newInstance(shape);
        } else {
          returnObject = returnType.getConstructor().newInstance();
        }
      } catch (InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException
          | NoSuchMethodException | SecurityException e) {
        LOGGER.error("Could not create {}", shapeType.getClass(), e);
      }
      cacheReturnObjects.put(shape, returnObject);
    }
    return cacheReturnObjects.get(shape);
  }
}
