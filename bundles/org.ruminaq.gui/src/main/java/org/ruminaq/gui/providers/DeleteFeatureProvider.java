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

import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.DeleteFeature;
import org.ruminaq.gui.DeleteForbiddenFeature;
import org.ruminaq.gui.DeleteLabelFeature;
import org.ruminaq.gui.api.GuiExtensionHandler;

public class DeleteFeatureProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public DeleteFeatureProvider(IFeatureProvider fp) {
		super(fp);
	}

	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		IDeleteFeature feature = extensions.getDeleteFeature(context, getFeatureProvider());
		if(feature != null) return feature;

	    String labelProperty = Graphiti.getPeService().getPropertyValue(context.getPictogramElement(), Constants.LABEL_PROPERTY);
	    if(labelProperty != null) return new DeleteLabelFeature(getFeatureProvider());

	    String portProperty      = Graphiti.getPeService().getPropertyValue(context.getPictogramElement(), Constants.INTERNAL_PORT);
	    String canDeleteProperty = Graphiti.getPeService().getPropertyValue(context.getPictogramElement(), Constants.CAN_DELETE);
	    if(portProperty != null && canDeleteProperty == null) return new DeleteForbiddenFeature(getFeatureProvider());

		return new DeleteFeature(getFeatureProvider());
	}

}
