/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	static class Filter implements FeaturePredicate<IContext> {
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
