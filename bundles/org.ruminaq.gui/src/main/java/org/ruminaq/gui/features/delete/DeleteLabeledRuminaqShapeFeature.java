/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.delete;

import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.delete.DeleteLabeledRuminaqShapeFeature.Filter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;

@FeatureFilter(Filter.class)
public class DeleteLabeledRuminaqShapeFeature
    extends DeleteRuminaqShapeFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context) {
      return Optional.of(context).filter(IDeleteContext.class::isInstance)
          .map(IDeleteContext.class::cast)
          .map(IDeleteContext::getPictogramElement)
          .filter(LabeledRuminaqShape.class::isInstance).isPresent();
    }
  }

  public DeleteLabeledRuminaqShapeFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public void preDelete(IDeleteContext context) {
    super.preDelete(context);
    Optional.of(context).map(IDeleteContext::getPictogramElement)
        .filter(LabeledRuminaqShape.class::isInstance)
        .map(LabeledRuminaqShape.class::cast).map(LabeledRuminaqShape::getLabel)
        .ifPresent((LabelShape l) -> {
          RemoveContext ctx = new RemoveContext(l);
          getFeatureProvider().getRemoveFeature(ctx).remove(ctx);
        });
  }
}
