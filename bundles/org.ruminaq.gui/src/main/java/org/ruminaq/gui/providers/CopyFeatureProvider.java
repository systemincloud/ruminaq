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

import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.CopyElementFeature;
import org.ruminaq.model.ruminaq.BaseElement;

public class CopyFeatureProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public CopyFeatureProvider(IFeatureProvider fp) {
		super(fp);
	}

	public ICopyFeature getCopyFeature(ICopyContext context) {
	    PictogramElement[] pes = context.getPictogramElements();

	    boolean allBaseElements = true;
	    for (PictogramElement pe : pes) {
	    	if(pe instanceof Shape && Graphiti.getPeService().getPropertyValue(pe, Constants.SIMPLE_CONNECTION_POINT) != null) {
	    		continue;
	    	}
		    Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		    if (!(bo instanceof BaseElement)) {
		    	allBaseElements = false;
		    }
	    }

		ICopyFeature feature = extensions.getCopyFeature(context, getFeatureProvider());
		if (feature != null) {
			return feature;
		} else if (allBaseElements) {
			return new CopyElementFeature(getFeatureProvider());
		} else {
			return null;
		}
	}
}
