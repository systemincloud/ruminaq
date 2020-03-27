/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.features.PasteFeatureFilter;
import org.ruminaq.gui.features.paste.RuminaqShapePasteFeature;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.util.Result;

/**
 * Service api providing graphiti AddFeature.
 *
 * @author Marek Jagielski
 */
public interface PasteElementFeatureExtension extends
    BestFeatureExtension<RuminaqShapePasteFeature<? extends RuminaqShape>> {

  /**
   * Create RuminaqShapePasteFeatures.
   * 
   * @param features
   * @param fp       IFeatureProvider of Graphiti
   * @param oldPe    original PictogramElement
   * @param xMin     the smallest x position of all copied elements
   * @param yMin     the smallest y position of all copied elements
   * @return
   */
  default List<RuminaqShapePasteFeature<? extends RuminaqShape>> createFeatures(
      List<Class<? extends RuminaqShapePasteFeature<? extends RuminaqShape>>> features,
      IFeatureProvider fp, PictogramElement oldPe, int xMin, int yMin) {
    return Optional.ofNullable(features).orElseGet(() -> Collections.emptyList())
        .stream()
        .map(f -> Result.attempt(() -> f.getConstructor(IFeatureProvider.class,
            PictogramElement.class, Integer.TYPE, Integer.TYPE)))
        .map(r -> r.orElse(null)).filter(Objects::nonNull)
        .map(f -> Result.attempt(() -> f.newInstance(fp, oldPe, xMin, yMin)))
        .map(r -> r.orElse(null)).filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Get first found RuminaqShapePasteFeature.
   * 
   * @param fp    IFeatureProvider of Graphiti
   * @param oldBo original model object
   * @param oldPe original PictogramElement
   * @param xMin  the smallest x position of all copied elements
   * @param yMin  the smallest y position of all copied elements
   * @return
   */
  default RuminaqShapePasteFeature<? extends RuminaqShape> getFeature(
      IFeatureProvider fp, BaseElement oldBo, PictogramElement oldPe, int xMin,
      int yMin) {
    return createFeatures(getFeatures().stream().filter(filter(oldBo, fp))
        .findFirst().stream().collect(Collectors.toList()), fp, oldPe, xMin,
        yMin).stream().findFirst().orElse(null);
  }

  /**
   * Check if PasteFeatureFilter matches for BaseElement.
   * 
   * @param oldBo domain element
   * @param fp IFeatureProvider of Graphiti
   * @return predicate determining if class matches the filter
   */
  default Predicate<Class<? extends RuminaqShapePasteFeature<? extends RuminaqShape>>> filter(
      BaseElement oldBo, IFeatureProvider fp) {
    return clazz -> Optional
        .ofNullable(clazz.getAnnotation(PasteFeatureFilter.class))
        .map(PasteFeatureFilter::value)
        .map(f -> Result.attempt(f::getConstructor))
        .flatMap(r -> Optional.ofNullable(r.orElse(null)))
        .map(f -> Result.attempt(f::newInstance))
        .flatMap(r -> Optional.ofNullable(r.orElse(null)))
        .map(f -> f.test(oldBo, fp)).orElse(Boolean.TRUE);
  }
}
