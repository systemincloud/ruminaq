/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.gui.api;

import java.util.Collection;
import java.util.Map;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveAnchorFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveAnchorContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.ruminaq.gui.features.tools.IContextButtonPadTool;


/**
 *
 * @author Marek Jagielski
 */
public interface GuiExtensionHandler {
	Map<String, IPaletteCompartmentEntry> getPaletteCompartmentEntries(ICreateFeature[] cfs);

	Map<String, IPaletteCompartmentEntry> getTestPaletteCompartmentEntries(ICreateFeature[] cfs);

	Collection<ICreateFeature> getCreateFeatures(IFeatureProvider fp);

	Collection<ICreateConnectionFeature> getCreateConnectionFeatures(IFeatureProvider fp);

	Collection<ICustomFeature> getCustomFeatures(IFeatureProvider fp);

	Collection<IContextButtonPadTool> getContextButtonPadTools(Object bo);

	Collection<IDecorator> getDecorators(PictogramElement pe, IFeatureProvider fp);

	Collection<IContextMenuEntry> getContextMenu(ICustomContext cxt, IFeatureProvider fp);

	IAddFeature getAddFeature(IAddContext cxt, IFeatureProvider fp);

	IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext cxt, IFeatureProvider fp);

	IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext cxt, IFeatureProvider fp);

	IMoveAnchorFeature getMoveAnchorFeature(IMoveAnchorContext cxt, IFeatureProvider fp);

	IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext cxt, IFeatureProvider fp);

	IUpdateFeature getUpdateFeature(IUpdateContext cxt, IFeatureProvider fp);

	ILayoutFeature getLayoutFeature(ILayoutContext cxt, IFeatureProvider fp);

	IReconnectionFeature getReconnectionFeature(IReconnectionContext cxt, IFeatureProvider fp);

	IDeleteFeature getDeleteFeature(IDeleteContext cxt, IFeatureProvider fp);

	ICustomFeature getDoubleClickFeature(IDoubleClickContext cxt, IFeatureProvider fp);

	ICopyFeature getCopyFeature(ICopyContext ctx, IFeatureProvider fp);

	IPasteFeature getPasteFeature(IPasteContext ctx, IFeatureProvider fp);

	Map<String, String> getImageKeyPath();

}
