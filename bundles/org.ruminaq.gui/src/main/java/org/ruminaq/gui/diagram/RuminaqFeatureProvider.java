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

import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddBendpointFeature;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveAnchorFeature;
import org.eclipse.graphiti.features.IMoveBendpointFeature;
import org.eclipse.graphiti.features.IMoveConnectionDecoratorFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IPrintFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveBendpointFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.ISaveImageFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddBendpointContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveAnchorContext;
import org.eclipse.graphiti.features.context.IMoveBendpointContext;
import org.eclipse.graphiti.features.context.IMoveConnectionDecoratorContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IRemoveBendpointContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.ruminaq.gui.providers.AddFeatureProvider;
import org.ruminaq.gui.providers.CopyFeatureProvider;
import org.ruminaq.gui.providers.CreateConnectionFeatureProvider;
import org.ruminaq.gui.providers.CreateFeatureProvider;
import org.ruminaq.gui.providers.CustomFeatureProvider;
import org.ruminaq.gui.providers.DeleteFeatureProvider;
import org.ruminaq.gui.providers.DirectEditingFeatureProvider;
import org.ruminaq.gui.providers.LayoutFeatureProvider;
import org.ruminaq.gui.providers.MoveAnchorFeatureProvider;
import org.ruminaq.gui.providers.MoveShapeFeatureProvider;
import org.ruminaq.gui.providers.PasteFeatureProvider;
import org.ruminaq.gui.providers.ReconnectionFeatureProvider;
import org.ruminaq.gui.providers.ResizeShapeFeatureProvider;
import org.ruminaq.gui.providers.UpdateFeatureProvider;

public class RuminaqFeatureProvider extends DefaultFeatureProvider {

	public RuminaqFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {	super(diagramTypeProvider);	}

   	@Override
   	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
   	    return null; // remove disabled for the UI
   	}

   	protected IRemoveFeature getRemoveFeatureEnabled(IRemoveContext context) {
   	    return super.getRemoveFeature(context); // used where we enable remove (deleting...)
   	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		IAddFeature feature = (new AddFeatureProvider(this)).getAddFeature(context);
		return feature != null ? feature : super.getAddFeature(context);
	}

	@Override
	public IAddBendpointFeature getAddBendpointFeature(IAddBendpointContext context) {
		return super.getAddBendpointFeature(context);
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		List<ICreateConnectionFeature> connectionFeatures = (new CreateConnectionFeatureProvider(this)).getCreateConnectionFeatures();
		return connectionFeatures.toArray(new ICreateConnectionFeature[connectionFeatures.size()]);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		List<ICreateFeature> standardFeatures = (new CreateFeatureProvider(this)).getCreateFeatures();
		return standardFeatures.toArray(new ICreateFeature[standardFeatures.size()]);
	}

	@Override
	public ICopyFeature getCopyFeature(ICopyContext context) {
		ICopyFeature feature = (new CopyFeatureProvider(this)).getCopyFeature(context);
		return feature != null ? feature : super.getCopyFeature(context);
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		List<ICustomFeature> customFeatures = (new CustomFeatureProvider(this)).getCustomFeatures(context);
		return customFeatures.toArray(new ICustomFeature[customFeatures.size()]);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		IDeleteFeature feature = (new DeleteFeatureProvider(this)).getDeleteFeature(context);
		if(feature != null) return feature;	else return super.getDeleteFeature(context);
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		IDirectEditingFeature feature = (new DirectEditingFeatureProvider(this)).getDirectEditingFeature(context);
		return feature != null ? feature : super.getDirectEditingFeature(context);
	}

	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		return super.getDragAndDropFeatures(context);
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		ILayoutFeature feature = (new LayoutFeatureProvider(this)).getLayoutFeature(context);
		return feature != null ? feature : super.getLayoutFeature(context);
	}

	@Override
	public IMoveAnchorFeature getMoveAnchorFeature(IMoveAnchorContext context) {
		IMoveAnchorFeature feature = (new MoveAnchorFeatureProvider(this)).getMoveAnchorFeature(context);
		return feature != null ? feature : super.getMoveAnchorFeature(context);
	}

	@Override
	public IMoveBendpointFeature getMoveBendpointFeature(IMoveBendpointContext context) {
		return super.getMoveBendpointFeature(context);
	}

	@Override
	public IMoveConnectionDecoratorFeature getMoveConnectionDecoratorFeature(IMoveConnectionDecoratorContext context) {
		return super.getMoveConnectionDecoratorFeature(context);
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		IMoveShapeFeature feature = (new MoveShapeFeatureProvider(this)).getMoveShapeFeature(context);
		return feature;
	}

	@Override
	public IPasteFeature getPasteFeature(IPasteContext context) {
		IPasteFeature feature = (new PasteFeatureProvider(this)).getPasteFeature(context);
		return feature != null ? feature : super.getPasteFeature(context);
	}

	@Override
	public IPrintFeature getPrintFeature() {
		return super.getPrintFeature();
	}

	@Override
	public ISaveImageFeature getSaveImageFeature() {
		return super.getSaveImageFeature();
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
		IReconnectionFeature feature = (new ReconnectionFeatureProvider(this)).getReconnectionFeature(context);
		return feature != null ? feature : super.getReconnectionFeature(context);
	}

	@Override
	public IRemoveBendpointFeature getRemoveBendpointFeature(IRemoveBendpointContext context) {
		return super.getRemoveBendpointFeature(context);
	}

	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		IResizeShapeFeature feature = (new ResizeShapeFeatureProvider(this)).getResizeShapeFeature(context);
		return feature != null ? feature : super.getResizeShapeFeature(context);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		IUpdateFeature feature = (new UpdateFeatureProvider(this)).getUpdateFeature(context);
		return feature != null ? feature : super.getUpdateFeature(context);
	}
}
