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
package org.ruminaq.tasks.constant.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.constant.impl.Port;
import org.ruminaq.tasks.features.AddTaskFeature;

public class AddFeature extends AddTaskFeature {

	public AddFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected int getHeight() {
		return 50;
	}

	@Override
	protected int getWidth() {
		return 50;
	}

	@Override
	protected Class<? extends PortsDescr> getPortsDescription() {
		return Port.class;
	}
}
