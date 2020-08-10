/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.delete;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.delete.DeleteRuminaqShapeFeature.Filter;
import org.ruminaq.gui.model.diagram.RuminaqShape;

/**
 * Delete RuminaqShape. First delete connections.
 * Lastly delete domain object.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class DeleteRuminaqShapeFeature extends RuminaqDeleteFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context) {
      return Optional.of(context).filter(IDeleteContext.class::isInstance)
          .map(IDeleteContext.class::cast)
          .map(IDeleteContext::getPictogramElement)
          .filter(RuminaqShape.class::isInstance).isPresent();
    }
  }

  public DeleteRuminaqShapeFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public void preDelete(IDeleteContext context) {
    Supplier<Stream<Anchor>> anchors = () -> Optional.of(context)
        .map(IDeleteContext::getPictogramElement)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getAnchors).map(EList::stream)
        .orElseGet(Stream::empty);
    anchors.get().map(Anchor::getIncomingConnections).flatMap(EList::stream)
        .collect(Collectors.toList()).forEach((Connection ic) -> {
          DeleteContext ctx = new DeleteContext(ic);
          ctx.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
          getFeatureProvider().getDeleteFeature(ctx).delete(ctx);
        });
    anchors.get().map(Anchor::getOutgoingConnections).flatMap(EList::stream)
        .collect(Collectors.toList()).forEach((Connection ic) -> {
          DeleteContext ctx = new DeleteContext(ic);
          ctx.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
          getFeatureProvider().getDeleteFeature(ctx).delete(ctx);
        });
  }

  @Override
  public boolean canDelete(IDeleteContext context) {
    return true;
  }

  @Override
  public void postDelete(IDeleteContext context) {
    Optional.of(context).map(IDeleteContext::getPictogramElement)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject)
        .ifPresent(mo -> EcoreUtil.delete(mo, true));
  }
}
