/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.delete;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.delete.DeleteForbiddenFeature.Filter;

@FeatureFilter(Filter.class)
public class DeleteForbiddenFeature extends DefaultDeleteFeature {

	public static class Filter implements FeaturePredicate<IContext> {
		@Override
		public boolean test(IContext context) {
			IDeleteContext deleteContext = (IDeleteContext) context;
			String portProperty = Graphiti.getPeService().getPropertyValue(
			    deleteContext.getPictogramElement(), Constants.INTERNAL_PORT);
			String canDeleteProperty = Graphiti.getPeService().getPropertyValue(
			    deleteContext.getPictogramElement(), Constants.CAN_DELETE);
			return portProperty != null && canDeleteProperty == null;
		}
	}

	public DeleteForbiddenFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		return false;
	}
}
