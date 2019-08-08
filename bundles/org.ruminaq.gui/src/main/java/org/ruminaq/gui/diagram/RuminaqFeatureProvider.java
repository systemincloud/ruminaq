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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
import org.eclipse.graphiti.features.context.IContext;
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
import org.ruminaq.gui.api.AddFeatureExtension;
import org.ruminaq.gui.api.BestFeatureExtension;
import org.ruminaq.gui.api.CopyFeatureExtension;
import org.ruminaq.gui.api.CreateConnectionFeaturesExtension;
import org.ruminaq.gui.api.CreateFeaturesExtension;
import org.ruminaq.gui.api.CustomFeaturesExtension;
import org.ruminaq.gui.api.DeleteFeatureExtension;
import org.ruminaq.gui.api.DirectEditingFeatureExtension;
import org.ruminaq.gui.api.LayoutFeatureExtension;
import org.ruminaq.gui.api.MoveAnchorFeatureExtension;
import org.ruminaq.gui.api.MoveShapeFeatureExtension;
import org.ruminaq.gui.api.MultipleFeaturesExtension;
import org.ruminaq.gui.api.PasteFeatureExtension;
import org.ruminaq.gui.api.ReconnectionFeatureExtension;
import org.ruminaq.gui.api.ResizeShapeFeatureExtension;
import org.ruminaq.gui.api.UpdateFeatureExtension;
import org.ruminaq.util.ServiceUtil;

public class RuminaqFeatureProvider extends DefaultFeatureProvider {

	public RuminaqFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return null; // remove disabled for the UI
	}

	protected IRemoveFeature getRemoveFeatureEnabled(IRemoveContext context) {
		return super.getRemoveFeature(context); // used where we enable remove
		                                        // (deleting...)
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		return getFeature(IAddFeature.class, AddFeatureExtension.class, context,
		    () -> super.getAddFeature(context));
	}

	@Override
	public IAddBendpointFeature getAddBendpointFeature(
	    IAddBendpointContext context) {
		return super.getAddBendpointFeature(context);
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return getFeatures(ICreateConnectionFeature.class,
		    CreateConnectionFeaturesExtension.class).stream()
		        .toArray(ICreateConnectionFeature[]::new);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return getFeatures(ICreateFeature.class, CreateFeaturesExtension.class)
		    .toArray(ICreateFeature[]::new);
	}

	@Override
	public ICopyFeature getCopyFeature(ICopyContext context) {
		return getFeature(ICopyFeature.class, CopyFeatureExtension.class, context,
		    () -> super.getCopyFeature(context));
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return getFeatures(ICustomFeature.class, CustomFeaturesExtension.class)
		    .toArray(ICustomFeature[]::new);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		return getFeature(IDeleteFeature.class, DeleteFeatureExtension.class,
		    context, () -> super.getDeleteFeature(context));
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(
	    IDirectEditingContext context) {
		return getFeature(IDirectEditingFeature.class,
		    DirectEditingFeatureExtension.class, context,
		    () -> super.getDirectEditingFeature(context));
	}

	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		return super.getDragAndDropFeatures(context);
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		return getFeature(ILayoutFeature.class, LayoutFeatureExtension.class,
		    context, () -> super.getLayoutFeature(context));
	}

	@Override
	public IMoveAnchorFeature getMoveAnchorFeature(IMoveAnchorContext context) {
		return getFeature(IMoveAnchorFeature.class,
		    MoveAnchorFeatureExtension.class, context,
		    () -> super.getMoveAnchorFeature(context));
	}

	@Override
	public IMoveBendpointFeature getMoveBendpointFeature(
	    IMoveBendpointContext context) {
		return super.getMoveBendpointFeature(context);
	}

	@Override
	public IMoveConnectionDecoratorFeature getMoveConnectionDecoratorFeature(
	    IMoveConnectionDecoratorContext context) {
		return super.getMoveConnectionDecoratorFeature(context);
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		return getFeature(IMoveShapeFeature.class, MoveShapeFeatureExtension.class,
		    context, () -> super.getMoveShapeFeature(context));
	}

	@Override
	public IPasteFeature getPasteFeature(IPasteContext context) {
		return getFeature(IPasteFeature.class, PasteFeatureExtension.class, context,
		    () -> super.getPasteFeature(context));
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
	public IReconnectionFeature getReconnectionFeature(
	    IReconnectionContext context) {
		return getFeature(IReconnectionFeature.class,
		    ReconnectionFeatureExtension.class, context,
		    () -> super.getReconnectionFeature(context));
	}

	@Override
	public IRemoveBendpointFeature getRemoveBendpointFeature(
	    IRemoveBendpointContext context) {
		return super.getRemoveBendpointFeature(context);
	}

	@Override
	public IResizeShapeFeature getResizeShapeFeature(
	    IResizeShapeContext context) {
		return getFeature(IResizeShapeFeature.class,
		    ResizeShapeFeatureExtension.class, context,
		    () -> super.getResizeShapeFeature(context));
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		return getFeature(IUpdateFeature.class, UpdateFeatureExtension.class,
		    context, () -> super.getUpdateFeature(context));
	}

	private <T extends IFeature> List<T> getFeatures(Class<T> outputClass,
	    Class<? extends MultipleFeaturesExtension<T>> serviceClass) {
		return ServiceUtil
		    .getServicesAtLatestVersion(RuminaqFeatureProvider.class, serviceClass)
		    .stream().map(ext -> ext.getFeatures(this)).flatMap(Collection::stream)
		    .collect(Collectors.toList());
	}

	private <T extends IFeature> T getFeature(Class<T> outputClass,
	    Class<? extends BestFeatureExtension<T>> serviceClass, IContext context,
	    Supplier<? extends T> superMethod) {
		return ServiceUtil
		    .getServicesAtLatestVersion(RuminaqFeatureProvider.class, serviceClass)
		    .stream().map(ext -> ext.getFeature(context, this))
		    .filter(Objects::nonNull).findFirst().orElseGet(superMethod);
	}
}
