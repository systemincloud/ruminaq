/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ruminaq.model.ruminaq.BaseElement;

/**
 * Annotation for feature class providers that specify filter if providers
 * relates.
 *
 * @author Marek Jagielski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModelFeatureFilter {
  /**
   * Class that implements filter.
   *
   * @return class
   */
  Class<? extends FeaturePredicate<BaseElement>> value();
}
