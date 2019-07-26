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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.ISingleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.ruminaq.eclipse.util.ConstantsUtil;
import org.ruminaq.gui.providers.ContextButtonPadDataProvider;
import org.ruminaq.gui.providers.ContextMenuEntryProvider;
import org.ruminaq.gui.providers.DecoratorProvider;
import org.ruminaq.gui.providers.DoubleClickFeatureProvider;
import org.ruminaq.gui.providers.PaletteCompartmentEntryProvider;
import org.ruminaq.gui.providers.TestPaletteCompartmentEntryProvider;

public class RuminaqBehaviorProvider extends DefaultToolBehaviorProvider {

	public RuminaqBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	public boolean isShowSelectionTool() {
		return false;
	}

	@Override
	public boolean isShowMarqueeTool() {
		return false;
	}

	@Override
	public boolean isShowGuides() {
		return false;
	}

	@Override
	public boolean equalsBusinessObjects(Object o1, Object o2) {
		return o1 instanceof EObject && o2 instanceof EObject ? o1 == o2 : false;
	}

	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		IPaletteCompartmentEntry[] entries;
		if (ConstantsUtil
		    .isTest(getDiagramTypeProvider().getDiagram().eResource().getURI()))
			entries = (new TestPaletteCompartmentEntryProvider(getFeatureProvider()))
			    .getPalette();
		else
			entries = (new PaletteCompartmentEntryProvider(getFeatureProvider()))
			    .getPalette();
		return entries != null ? entries : super.getPalette();
	}

	@Override
	public IContextButtonPadData getContextButtonPad(
	    IPictogramElementContext context) {
		IContextButtonPadData data = super.getContextButtonPad(context);
		return (new ContextButtonPadDataProvider(getFeatureProvider()))
		    .getContextButtonPad(context, this, data);
	}

	public void setGenericContextButtonsProxy(IContextButtonPadData data,
	    PictogramElement pe, int i) {
		super.setGenericContextButtons(data, pe, i);
	}

	@Override
	public IContextMenuEntry[] getContextMenu(ICustomContext context) {
		IContextMenuEntry[] entries = (new ContextMenuEntryProvider(
		    getFeatureProvider())).getContextMenu(context);
		return entries != null ? entries : super.getContextMenu(context);
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		ICustomFeature feature = (new DoubleClickFeatureProvider(
		    getFeatureProvider())).getDoubleClickFeature(context);
		return feature != null ? feature : super.getDoubleClickFeature(context);
	}

	@Override
	public ICustomFeature getSingleClickFeature(ISingleClickContext context) {
		return super.getSingleClickFeature(context);
	}

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		IDecorator[] decorators = (new DecoratorProvider(getFeatureProvider()))
		    .getDecorators(pe);
		return decorators != null ? decorators : super.getDecorators(pe);
	}

	@Override
	public PictogramElement getSelection(PictogramElement originalPe,
	    PictogramElement[] oldSelection) {
		return null;
	}
}
