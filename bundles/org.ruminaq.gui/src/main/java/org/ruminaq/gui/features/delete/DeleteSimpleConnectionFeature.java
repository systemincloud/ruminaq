/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.delete;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.delete.DeleteSimpleConnectionFeature.Filter;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;

@FeatureFilter(Filter.class)
public class DeleteSimpleConnectionFeature extends RuminaqDeleteFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context) {
      return Optional.of(context).filter(IDeleteContext.class::isInstance)
          .map(IDeleteContext.class::cast)
          .map(IDeleteContext::getPictogramElement)
          .filter(SimpleConnectionShape.class::isInstance).isPresent();
    }
  }

  public DeleteSimpleConnectionFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canDelete(IDeleteContext context) {
    return true;
  }

  @Override
  public void preDelete(IDeleteContext context) {
    Optional.of(context).map(IDeleteContext::getPictogramElement)
        .filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast)
        .map(SimpleConnectionShape::getTarget)
        .filter(SimpleConnectionPointShape.class::isInstance).ifPresent(scp -> {
          DeleteContext deleteCtx = new DeleteContext(scp);
          deleteCtx.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
          IDeleteFeature deleteFeature = getFeatureProvider()
              .getDeleteFeature(deleteCtx);
          deleteFeature.delete(deleteCtx);
        });
  }

  @Override
  public void delete(IDeleteContext context) {
    Optional.of(context).map(IDeleteContext::getPictogramElement)
        .filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast)
        .map(SimpleConnectionShape::getModelObject).map(List::stream)
        .orElseGet(Stream::empty).collect(Collectors.toList())
        .forEach(mo -> EcoreUtil.delete(mo, true));
    super.delete(context);
  }
}
