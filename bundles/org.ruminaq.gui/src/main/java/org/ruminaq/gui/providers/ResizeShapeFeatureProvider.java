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
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.resize.ResizeShapeFeature;
import org.ruminaq.gui.features.resize.ResizeShapeForbiddenFeature;
import org.ruminaq.gui.features.util.FeatureUtil;
import org.ruminaq.model.config.CommonConfig;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.Port;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.TaskProvider;
import org.ruminaq.tasks.features.ResizeShapeTaskFeature;

public class ResizeShapeFeatureProvider extends FeatureProvider {

	public ResizeShapeFeatureProvider(IFeatureProvider fp) {
		super(fp);
	}

	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		String labelProperty = Graphiti.getPeService().getPropertyValue(context.getShape(),	Constants.LABEL_PROPERTY);
		if(Boolean.parseBoolean(labelProperty)) return new ResizeShapeForbiddenFeature(getFeatureProvider());

		String connectionPointProperty = Graphiti.getPeService().getPropertyValue(context.getShape(), Constants.SIMPLE_CONNECTION_POINT);
		if(Boolean.parseBoolean(connectionPointProperty)) return new ResizeShapeForbiddenFeature(getFeatureProvider());

		Shape shape = context.getShape();
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(shape);

		IResizeShapeFeature feature = extensions.getResizeShapeFeature(context, getFeatureProvider());
		if(feature != null) return feature;
		IResizeShapeFeature taskFeature = TaskProvider          .INSTANCE.getResizeShapeFeature(context, getFeatureProvider());
		if(taskFeature != null) return taskFeature;

		if      (bo instanceof InternalPort)	return new ResizeShapeForbiddenFeature(getFeatureProvider());
		else if (bo instanceof Port)			return new ResizeShapeForbiddenFeature(getFeatureProvider());
		else {
			IResizeShapeFeature localFeature = FeatureUtil.getLocalFeature(ResizeShapeFeature.class,
					                                                       IResizeShapeFeature.class,
                                                                           CommonConfig.getInstance(),
                                                                           bo,
                                                                           getFeatureProvider());
			if(localFeature != null) return localFeature;
		}
		if (bo instanceof Task)	return new ResizeShapeTaskFeature(getFeatureProvider());
		else return null;
	}
}
