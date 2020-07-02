/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features;

import java.util.function.Predicate;

import org.eclipse.graphiti.features.IFeatureProvider;

/**
 * Interface for predicates used in FeatureFilter.
 *
 * @author Marek Jagielski
 */
public interface FeaturePredicate<T> extends Predicate<T> {

  default boolean test(T context, IFeatureProvider fp) {
    return test(context);
  }

  @Override
  default boolean test(T context) {
    return true;
  }

  static <T> Predicate<T> predicate(Predicate<T> predicate) {
    return predicate;
  }
}
