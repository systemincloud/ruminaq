/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.graphiti.features.IFeatureProvider;

/**
 * Super interface for osgi service interfaces that contributes all
 * matching Graphiti feature.
 *
 * @author Marek Jagielski
 *
 * @param <T> feature java interface
 */
public interface MultipleFeaturesExtension<T> {

  default List<T> createFeatures(List<Class<? extends T>> features,
      IFeatureProvider fp) {
    return Optional.ofNullable(features).orElse(Collections.emptyList())
        .stream().<Constructor<? extends T>>map(f -> {
          try {
            return f.getConstructor(IFeatureProvider.class);
          } catch (NoSuchMethodException | SecurityException e) {
            return null;
          }
        }).filter(Objects::nonNull).<T>map(c -> {
          try {
            return c.newInstance(fp);
          } catch (InstantiationException | IllegalAccessException
              | IllegalArgumentException | InvocationTargetException e) {
            return null;
          }
        }).filter(Objects::nonNull).collect(Collectors.toList());
  }

  default List<T> getFeatures(IFeatureProvider fp) {
    return createFeatures(getFeatures(), fp);
  }

  default List<Class<? extends T>> getFeatures() {
    return Collections.emptyList();
  }
}
