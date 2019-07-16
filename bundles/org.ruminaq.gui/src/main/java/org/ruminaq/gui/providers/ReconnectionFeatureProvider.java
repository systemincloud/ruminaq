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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.reconnection.ReconnectionSimpleConnectionFeature;
import org.ruminaq.model.ruminaq.SimpleConnection;

public class ReconnectionFeatureProvider extends FeatureProvider {

	public ReconnectionFeatureProvider(IFeatureProvider fp) {
		super(fp);
	}

	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
		Connection c = context.getConnection();
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(c);

		IReconnectionFeature feature = extensions.getReconnectionFeature(context, getFeatureProvider());
		if(feature != null) return feature;
		else if(bo instanceof SimpleConnection) return new ReconnectionSimpleConnectionFeature(getFeatureProvider());
		else return null;
	}

}
