/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.diagram;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveAnchorFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveAnchorContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
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

/**
 * Features controller.
 *
 * @author Marek Jagielski
 */
public class RuminaqFeatureProvider extends DefaultFeatureProvider {

  public RuminaqFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
    super(diagramTypeProvider);
  }

  /**
   * Just removing elements not allowed.
   */
  @Override
  public IRemoveFeature getRemoveFeature(IRemoveContext context) {
    return null;
  }

  /**
   * Used where we enable remove (deleting...).
   *
   * @param context IRemoveContext graphiti
   * @return IRemoveFeature graphiti
   */
  protected IRemoveFeature getRemoveFeatureEnabled(IRemoveContext context) {
    return super.getRemoveFeature(context);
  }

  @Override
  public IAddFeature getAddFeature(IAddContext context) {
    return getFeature(AddFeatureExtension.class, context,
        () -> super.getAddFeature(context));
  }

  @Override
  public ICreateConnectionFeature[] getCreateConnectionFeatures() {
    return getFeatures(CreateConnectionFeaturesExtension.class).stream()
        .toArray(ICreateConnectionFeature[]::new);
  }

  @Override
  public ICreateFeature[] getCreateFeatures() {
    return getFeatures(CreateFeaturesExtension.class)
        .toArray(ICreateFeature[]::new);
  }

  @Override
  public ICopyFeature getCopyFeature(ICopyContext context) {
    return getFeature(CopyFeatureExtension.class, context,
        () -> super.getCopyFeature(context));
  }

  @Override
  public ICustomFeature[] getCustomFeatures(ICustomContext context) {
    return getFeatures(CustomFeaturesExtension.class)
        .toArray(ICustomFeature[]::new);
  }

  @Override
  public IDeleteFeature getDeleteFeature(IDeleteContext context) {
    return getFeature(DeleteFeatureExtension.class, context,
        () -> super.getDeleteFeature(context));
  }

  @Override
  public IDirectEditingFeature getDirectEditingFeature(
      IDirectEditingContext context) {
    return getFeature(DirectEditingFeatureExtension.class, context,
        () -> super.getDirectEditingFeature(context));
  }

  @Override
  public ILayoutFeature getLayoutFeature(ILayoutContext context) {
    return getFeature(LayoutFeatureExtension.class, context,
        () -> super.getLayoutFeature(context));
  }

  @Override
  public IMoveAnchorFeature getMoveAnchorFeature(IMoveAnchorContext context) {
    return getFeature(MoveAnchorFeatureExtension.class, context,
        () -> super.getMoveAnchorFeature(context));
  }

  @Override
  public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
    return getFeature(MoveShapeFeatureExtension.class, context,
        () -> super.getMoveShapeFeature(context));
  }

  @Override
  public IPasteFeature getPasteFeature(IPasteContext context) {
    return getFeature(PasteFeatureExtension.class, context,
        () -> super.getPasteFeature(context));
  }

  @Override
  public IReconnectionFeature getReconnectionFeature(
      IReconnectionContext context) {
    return getFeature(ReconnectionFeatureExtension.class, context,
        () -> super.getReconnectionFeature(context));
  }

  @Override
  public IResizeShapeFeature getResizeShapeFeature(
      IResizeShapeContext context) {
    return getFeature(ResizeShapeFeatureExtension.class, context,
        () -> super.getResizeShapeFeature(context));
  }

  @Override
  public IUpdateFeature getUpdateFeature(IUpdateContext context) {
    return getFeature(UpdateFeatureExtension.class, context,
        () -> super.getUpdateFeature(context));
  }

  /**
   * Reusable method gathering all features instances for giver service
   * extension class. For bundles with the same symbolic name that with higher
   * version will be considered.
   */
  private <T extends IFeature> List<T> getFeatures(
      Class<? extends MultipleFeaturesExtension<T>> serviceClass) {
    return ServiceUtil
        .getServicesAtLatestVersion(RuminaqFeatureProvider.class, serviceClass)
        .stream().map(ext -> ext.getFeatures(this)).flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private <T extends IFeature> T getFeature(
      Class<? extends BestFeatureExtension<T>> serviceClass, IContext context,
      Supplier<? extends T> superMethod) {
    return ServiceUtil
        .getServicesAtLatestVersion(RuminaqFeatureProvider.class, serviceClass)
        .stream().map(ext -> ext.getFeature(context, this))
        .filter(Objects::nonNull).findFirst().orElseGet(superMethod);
  }
}
