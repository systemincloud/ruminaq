/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.features.FeaturePredicate;

public class ResizeFilter<T extends Shape>
    implements FeaturePredicate<IContext> {

  private Class<T> clazz;

  public ResizeFilter(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public boolean test(IContext context, IFeatureProvider fp) {
    return Optional.ofNullable(context)
        .filter(IResizeShapeContext.class::isInstance)
        .map(IResizeShapeContext.class::cast)
        .map(IResizeShapeContext::getShape).filter(clazz::isInstance)
        .isPresent();
  }

}