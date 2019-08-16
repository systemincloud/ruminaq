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
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.delete.DeleteLabelFeature.Filter;

@FeatureFilter(Filter.class)
public class DeleteLabelFeature extends RuminaqDeleteFeature {

	public static class Filter implements FeaturePredicate<IContext> {
		@Override
		public boolean test(IContext context) {
			IDeleteContext deleteContext = (IDeleteContext) context;
			return Graphiti.getPeService().getPropertyValue(
			    deleteContext.getPictogramElement(),
			    Constants.LABEL_PROPERTY) != null;
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
		for (PictogramElement s : selection)
			if (Graphiti.getPeService().getPropertyValue(s,
			    Constants.LABEL_PROPERTY) == null)
				return true;
		return false;
	}

	@Override
	public void delete(IDeleteContext context) {
	}
}
