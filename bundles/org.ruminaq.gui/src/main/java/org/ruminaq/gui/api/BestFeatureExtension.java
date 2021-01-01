/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.util.Result;

/**
 * Super interface for osgi service interfaces that contributes the best
 * matching Graphiti feature.
 *
 * @author Marek Jagielski
 *
 * @param <T> feature java interface
 */
public interface BestFeatureExtension<T> extends MultipleFeaturesExtension<T> {

  /**
   * Create and return the first matched feature.
   *
   * @param context IContext of Graphiti
   * @param fp      IFeatureProvider of Graphiti
   * @return feature
   */
  default T getFeature(IContext context, IFeatureProvider fp) {
    return createFeatures(getFeatures().stream().filter(filter(context, fp))
        .findFirst().stream().collect(Collectors.toList()), fp).stream()
            .findFirst().orElse(null);
  }

  /**
   * Determine if Feature matches.
   *
   * @param context context of feature
   * @param fp      IFeatureProvider of Graphiti
   * @return predicate used by Extension to determine Feature
   */
  default Predicate<Class<? extends T>> filter(IContext context,
      IFeatureProvider fp) {
    return (Class<? extends T> clazz) -> Optional
        .ofNullable(clazz.getAnnotation(FeatureFilter.class))
        .map(FeatureFilter::value)
        .map(f -> Result.attempt(f::getDeclaredConstructor))
        .flatMap(r -> Optional.ofNullable(r.orElse(null)))
        .map((Constructor<? extends FeaturePredicate<IContext>> c) -> {
          c.setAccessible(true);
          return c;
        }).map(f -> Result.attempt(f::newInstance))
        .flatMap(r -> Optional.ofNullable(r.orElse(null)))
        .map(f -> f.test(context, fp)).orElse(Boolean.TRUE);
  }

}
