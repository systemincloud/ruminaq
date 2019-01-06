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
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.update.UpdateBaseElementFeature;
import org.ruminaq.gui.features.update.UpdateFeature;
import org.ruminaq.gui.features.update.UpdateMainTaskFeature;
import org.ruminaq.gui.features.util.FeatureUtil;
import org.ruminaq.gui.label.UpdateLabelFeature;
import org.ruminaq.model.config.CommonConfig;
import org.ruminaq.model.model.ruminaq.BaseElement;
import org.ruminaq.model.model.ruminaq.MainTask;
import org.ruminaq.tasks.TaskProvider;

public class UpdateFeatureProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public UpdateFeatureProvider(IFeatureProvider fp) { super(fp); }

	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		if(context.getPictogramElement() instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) context.getPictogramElement();
			String labelProperty = Graphiti.getPeService().getPropertyValue(cs,	Constants.LABEL_PROPERTY);
			if (Boolean.parseBoolean(labelProperty)) return new UpdateLabelFeature(getFeatureProvider());
		}

	    PictogramElement pe = context.getPictogramElement();
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if(bo == null) return null;

		IUpdateFeature localFeature     = FeatureUtil.getLocalFeature(UpdateFeature.class, IUpdateFeature.class, CommonConfig.getInstance(), bo, getFeatureProvider());
		IUpdateFeature taskFeature      = TaskProvider.INSTANCE.getUpdateFeature(context, getFeatureProvider());
		IUpdateFeature extensionFeature = extensions.getUpdateFeature(context, getFeatureProvider());
		if(extensionFeature != null)       return extensionFeature;
		else if(bo instanceof MainTask)    return new UpdateMainTaskFeature(getFeatureProvider());
		else if(localFeature != null)      return localFeature;
		else if(taskFeature != null)       return taskFeature;
		else if(bo instanceof BaseElement) return new UpdateBaseElementFeature(getFeatureProvider());
		else return null;
	}
}
