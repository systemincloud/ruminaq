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
package org.ruminaq.gui.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IDecorator;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.tasks.TaskProvider;

public class DecoratorProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public DecoratorProvider(IFeatureProvider fp) { super(fp); }

	public IDecorator[] getDecorators(PictogramElement pe) {
		List<IDecorator> decorators = new ArrayList<>();

		decorators.addAll(extensions.getDecorators(pe, getFeatureProvider()));
		decorators.addAll(TaskProvider.INSTANCE.getDecorators(getFeatureProvider(), pe));

		return decorators.toArray(new IDecorator[decorators.size()]);
	}
}
