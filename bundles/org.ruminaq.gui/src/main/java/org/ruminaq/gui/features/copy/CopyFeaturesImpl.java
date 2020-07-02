/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.copy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CopyFeatureExtension;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;

/**
 * Service CopyFeatureExtension implementation.
 * 
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class CopyFeaturesImpl implements CopyFeatureExtension {

  @Override
  public List<Class<? extends ICopyFeature>> getFeatures() {
    return Arrays.asList(CopyElementFeature.class);
  }

  @Override
  public Predicate<Class<? extends ICopyFeature>> filter(IContext context,
      IFeatureProvider fp) {
    return (Class<? extends ICopyFeature> clazz) -> Optional.of(context)
        .filter(ICopyContext.class::isInstance).map(ICopyContext.class::cast)
        .map(ICopyContext::getPictogramElements).map(Stream::of)
        .orElse(Stream.empty())
        .filter(Predicate.not(SimpleConnectionPointShape.class::isInstance))
        .filter(RuminaqShape.class::isInstance).findAny().isPresent();
  }
}
