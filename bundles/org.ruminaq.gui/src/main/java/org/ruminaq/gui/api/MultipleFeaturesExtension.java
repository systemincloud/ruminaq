/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.util.Result;

/**
 * Super interface for osgi service interfaces that contributes all
 * matching Graphiti feature.
 *
 * @author Marek Jagielski
 *
 * @param <T> feature java interface
 */
public interface MultipleFeaturesExtension<T> {

  /**
   * Create and return all features for classes.
   * 
   * @param features list of classes to instantiate
   * @param fp IFeatureProvider of Graphiti
   * @return collection of features
   */
  default Collection<T> createFeatures(List<Class<? extends T>> features,
      IFeatureProvider fp) {
    return Optional.ofNullable(features).orElseGet(() -> Collections.emptyList())
        .stream()
        .map(f -> Result.attempt(() -> f.getConstructor(IFeatureProvider.class)))
        .map(r -> Optional.ofNullable(r.orElse(null)))
        .flatMap(Optional::stream)
        .map(f -> Result.attempt(() -> f.newInstance(fp)))
        .map(r -> Optional.ofNullable(r.orElse(null)))
        .flatMap(Optional::stream)
        .collect(Collectors.toList());
  }

  /**
   * Return all features for classes.
   * 
   * @param fp IFeatureProvider of Graphiti
   * @return collection of features
   */
  default Collection<T> getFeatures(IFeatureProvider fp) {
    return createFeatures(getFeatures(), fp);
  }

  default List<Class<? extends T>> getFeatures() {
    return Collections.emptyList();
  }
}
