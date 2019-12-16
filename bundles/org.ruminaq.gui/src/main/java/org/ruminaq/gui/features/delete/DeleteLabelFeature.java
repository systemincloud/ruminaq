/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.delete;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.delete.DeleteLabelFeature.Filter;
import org.ruminaq.gui.model.diagram.LabelShape;

@FeatureFilter(Filter.class)
public class DeleteLabelFeature extends RuminaqDeleteFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context) {
      IDeleteContext deleteContext = (IDeleteContext) context;
      return LabelShape.class.isInstance(deleteContext.getPictogramElement());
    }
  }

  public DeleteLabelFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canDelete(IDeleteContext context) {
    return Stream
        .of(getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior()
            .getDiagramContainer().getSelectedPictogramElements())
        .anyMatch(Predicate.not(LabelShape.class::isInstance));
  }

  @Override
  public void delete(IDeleteContext context) {
    // label is deleted together with labeled shape
  }
}
