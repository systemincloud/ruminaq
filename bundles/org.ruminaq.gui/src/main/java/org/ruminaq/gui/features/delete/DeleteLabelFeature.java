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
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.delete.DeleteLabelFeature.Filter;
import org.ruminaq.gui.model.diagram.LabelShape;

/**
 * Label can't be deleted.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class DeleteLabelFeature extends RuminaqDeleteFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context) {
      return Optional.of(context).filter(IDeleteContext.class::isInstance)
          .map(IDeleteContext.class::cast)
          .map(IDeleteContext::getPictogramElement)
          .filter(LabelShape.class::isInstance).isPresent();
    }
  }

  public DeleteLabelFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canDelete(IDeleteContext context) {
    return false;
  }

}
