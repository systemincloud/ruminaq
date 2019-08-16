/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IRemoveContext;

public class FeatureProviderRemoveDecorator extends RuminaqFeatureProvider {

	public FeatureProviderRemoveDecorator(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return super.getRemoveFeatureEnabled(context);
	}
}
