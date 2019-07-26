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
