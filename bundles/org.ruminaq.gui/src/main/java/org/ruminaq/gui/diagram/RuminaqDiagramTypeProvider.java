/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class RuminaqDiagramTypeProvider extends AbstractDiagramTypeProvider
    implements IDiagramTypeProvider {

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public RuminaqDiagramTypeProvider() {
		setFeatureProvider(new RuminaqFeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] {
			    new RuminaqBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}

	@Override
	public boolean isAutoUpdateAtRuntime() {
		return true;
	}

	@Override
	public boolean isAutoUpdateAtRuntimeWhenEditorIsSaved() {
		return false;
	}

	@Override
	public boolean isAutoUpdateAtStartup() {
		return true;
	}

	@Override
	public boolean isAutoUpdateAtReset() {
		return true;
	}
}
