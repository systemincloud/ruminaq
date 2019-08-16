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
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;

/**
 * Super interface for osgi service interfaces that contributes the best
 * matching Graphiti feature
 *
 * @author Marek Jagielski
 */
public interface BestFeatureExtension<T> extends MultipleFeaturesExtension<T> {

  default T getFeature(IContext context, IFeatureProvider fp) {
    return createFeatures(getFeatures().stream().filter(filter(context, fp))
        .findFirst().stream().collect(Collectors.toList()), fp).stream()
            .findFirst().orElse(null);
  }

  default Predicate<? super Class<? extends T>> filter(IContext context,
      IFeatureProvider fp) {
    return clazz -> {
      return Optional.ofNullable(clazz.getAnnotation(FeatureFilter.class))
          .map(FeatureFilter::value)
          .<Constructor<? extends FeaturePredicate<IContext>>>map(f -> {
            try {
              return f.getConstructor();
            } catch (NoSuchMethodException | SecurityException e) {
              return null;
            }
          }).<FeaturePredicate<IContext>>map(c -> {
            try {
              return c.newInstance();
            } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
              return null;
            }
          }).orElse(new FeaturePredicate<IContext>() {
          }).test(context, fp);
    };
  }

  @Override
  default List<Class<? extends T>> getFeatures() {
    return Collections.emptyList();
  }
}
