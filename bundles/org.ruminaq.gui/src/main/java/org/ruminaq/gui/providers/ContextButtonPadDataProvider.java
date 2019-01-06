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
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.diagram.RuminaqBehaviorProvider;
import org.ruminaq.gui.features.contextpad.ContextButtonPadBaseElementTool;
import org.ruminaq.gui.features.contextpad.ContextButtonPadConnectionPointTool;
import org.ruminaq.gui.features.contextpad.ContextButtonPadFlowSourceTool;
import org.ruminaq.gui.features.contextpad.ContextButtonPadInternalPortTool;
import org.ruminaq.gui.features.contextpad.ContextButtonPadPortTool;
import org.ruminaq.gui.features.tools.IContextButtonPadTool;
import org.ruminaq.model.model.ruminaq.BaseElement;
import org.ruminaq.model.model.ruminaq.FlowSource;
import org.ruminaq.model.model.ruminaq.InternalPort;
import org.ruminaq.model.model.ruminaq.Port;
import org.ruminaq.tasks.TaskProvider;

public class ContextButtonPadDataProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public ContextButtonPadDataProvider(IFeatureProvider fp) {
		super(fp);
	}

	public IContextButtonPadData getContextButtonPad(IPictogramElementContext context, RuminaqBehaviorProvider toolBehaviorProvider, IContextButtonPadData data) {
		PictogramElement pe = context.getPictogramElement();

		String labelProperty = Graphiti.getPeService().getPropertyValue(pe,	Constants.LABEL_PROPERTY);
		String portLabelProperty = Graphiti.getPeService().getPropertyValue(pe,	Constants.PORT_LABEL_PROPERTY);
		if (Boolean.parseBoolean(labelProperty) || Boolean.parseBoolean(portLabelProperty)) {
			toolBehaviorProvider.setGenericContextButtonsProxy(data, pe, 0);
			return data;
		}

		toolBehaviorProvider.setGenericContextButtonsProxy(data, pe, Constants.CONTEXT_BUTTON_DELETE);

	    Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);

	    List<IContextButtonPadTool> tools = new ArrayList<>();

		String connectionPointProperty = Graphiti.getPeService().getPropertyValue(pe, Constants.SIMPLE_CONNECTION_POINT);
		if(Boolean.parseBoolean(connectionPointProperty)) {
			tools.add(new ContextButtonPadFlowSourceTool     (getFeatureProvider()));
			tools.add(new ContextButtonPadConnectionPointTool(getFeatureProvider()));
		}

	    if(bo instanceof FlowSource)   tools.add(new ContextButtonPadFlowSourceTool  (getFeatureProvider()));
	    if(bo instanceof BaseElement)  tools.add(new ContextButtonPadBaseElementTool (getFeatureProvider()));
	    if(bo instanceof InternalPort) tools.add(new ContextButtonPadInternalPortTool(getFeatureProvider()));
	    if(bo instanceof Port)         tools.add(new ContextButtonPadPortTool        (getFeatureProvider()));

	    tools.addAll(extensions.getContextButtonPadTools(bo));
	    tools.addAll(TaskProvider.INSTANCE.getContextButtonPadTools(fp, bo));

		for(IContextButtonPadTool t : tools) {
			data.getDomainSpecificContextButtons().addAll(t.getContextButtonPad(context));
			if(t.getGenericContextButtons() != -1) toolBehaviorProvider.setGenericContextButtonsProxy(data, pe, t.getGenericContextButtons());
			if(t.getPadLocation(data.getPadLocation().getRectangleCopy()) != null) data.getPadLocation().setRectangle(t.getPadLocation(data.getPadLocation().getRectangleCopy()));
		}

		return data;
	}

}
