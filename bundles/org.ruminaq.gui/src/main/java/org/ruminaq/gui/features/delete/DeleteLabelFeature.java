/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.delete;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.LabelUtil;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.delete.DeleteLabelFeature.Filter;

@FeatureFilter(Filter.class)
public class DeleteLabelFeature extends RuminaqDeleteFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context) {
      IDeleteContext deleteContext = (IDeleteContext) context;
      return LabelUtil.isLabel(deleteContext.getPictogramElement());
    }
  }

  public DeleteLabelFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canDelete(IDeleteContext context) {
    PictogramElement[] selection = getFeatureProvider().getDiagramTypeProvider()
        .getDiagramBehavior().getDiagramContainer()
        .getSelectedPictogramElements();
    for (PictogramElement pe : selection)
      if (!LabelUtil.isLabel(pe))
        return true;
    return false;
  }

  @Override
  public void delete(IDeleteContext context) {
  }
}
