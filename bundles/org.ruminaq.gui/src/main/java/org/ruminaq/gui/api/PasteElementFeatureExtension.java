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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.PasteFeatureFilter;
import org.ruminaq.gui.features.paste.RuminaqPasteFeature;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * Service api providing graphiti AddFeature.
 *
 * @author Marek Jagielski
 */
public interface PasteElementFeatureExtension
    extends BestFeatureExtension<RuminaqPasteFeature<? extends RuminaqShape>> {

  default List<RuminaqPasteFeature<? extends RuminaqShape>> createFeatures(
      List<Class<? extends RuminaqPasteFeature<? extends RuminaqShape>>> features, IFeatureProvider fp,
      PictogramElement oldPe, int xMin, int yMin) {
    return Optional.ofNullable(features).orElse(Collections.emptyList())
        .stream().<Constructor<? extends RuminaqPasteFeature<? extends RuminaqShape>>>map(f -> {
          try {
            return f.getConstructor(IFeatureProvider.class,
                PictogramElement.class, Integer.TYPE, Integer.TYPE);
          } catch (NoSuchMethodException | SecurityException e) {
            return null;
          }
        }).filter(Objects::nonNull).<RuminaqPasteFeature<? extends RuminaqShape>>map(c -> {
          try {
            return c.newInstance(fp, oldPe, xMin, yMin);
          } catch (InstantiationException | IllegalAccessException
              | IllegalArgumentException | InvocationTargetException e) {
            return null;
          }
        }).filter(Objects::nonNull).collect(Collectors.toList());
  }

  default RuminaqPasteFeature<? extends RuminaqShape> getFeature(IFeatureProvider fp, BaseElement oldBo,
      PictogramElement oldPe, int xMin, int yMin) {
    return createFeatures(getFeatures().stream().filter(filter(oldBo, fp))
        .findFirst().stream().collect(Collectors.toList()), fp, oldPe, xMin,
        yMin).stream().findFirst().orElse(null);
  }

  default Predicate<? super Class<? extends RuminaqPasteFeature<? extends RuminaqShape>>> filter(
      BaseElement oldBo, IFeatureProvider fp) {
    return clazz -> Optional
        .ofNullable(clazz.getAnnotation(PasteFeatureFilter.class))
        .map(PasteFeatureFilter::value)
        .<Constructor<? extends FeaturePredicate<BaseElement>>>map(f -> {
          try {
            return f.getConstructor();
          } catch (NoSuchMethodException | SecurityException e) {
            return null;
          }
        }).<FeaturePredicate<BaseElement>>map(c -> {
          try {
            return c.newInstance();
          } catch (InstantiationException | IllegalAccessException
              | IllegalArgumentException | InvocationTargetException e) {
            return null;
          }
        }).orElse(new FeaturePredicate<BaseElement>() {
        }).test(oldBo, fp);
  }
}
