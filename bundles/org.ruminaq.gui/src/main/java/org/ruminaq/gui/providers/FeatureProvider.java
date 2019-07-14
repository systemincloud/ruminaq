/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.providers;

import org.eclipse.graphiti.features.IFeatureProvider;

public abstract class FeatureProvider {

	protected IFeatureProvider fp = null;

	public FeatureProvider(IFeatureProvider fp) {
		this.fp = fp;
	}

	protected IFeatureProvider getFeatureProvider() {
		return fp;
	}
}
