/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.delete;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.delete.DeleteSimpleConnectionPointFeature.Filter;
import org.ruminaq.gui.model.diagram.FlowTargetShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;

/**
 * IDeleteFeature for SimpleConnectionPoint.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class DeleteSimpleConnectionPointFeature extends RuminaqDeleteFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context) {
      return Optional.of(context).filter(IDeleteContext.class::isInstance)
          .map(IDeleteContext.class::cast)
          .map(IDeleteContext::getPictogramElement)
          .filter(SimpleConnectionPointShape.class::isInstance).isPresent();
    }
  }

  public DeleteSimpleConnectionPointFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canDelete(IDeleteContext context) {
    return true;
  }

  /**
   * Extract shape from IDeleteContext.
   *
   * @param context IDeleteContext
   * @return shape
   */
  private static Optional<SimpleConnectionPointShape> shapeFromContext(
      IDeleteContext context) {
    return Optional.of(context)
        .map(IDeleteContext::getPictogramElement)
        .filter(SimpleConnectionPointShape.class::isInstance)
        .map(SimpleConnectionPointShape.class::cast);
  }

  /**
   * Replace SimpleConnectionPoint with bendpoint.
   * Leave just one outgoing connection.
   */
  @Override
  public void preDelete(IDeleteContext context) {
    Optional<SimpleConnectionPointShape> scpOpt = shapeFromContext(context);
    Optional<SimpleConnectionShape> incommingOpt = scpOpt
        .map(SimpleConnectionPointShape::getIncomingConnections)
        .map(List::stream).orElseGet(Stream::empty)
        .filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast).findFirst();
    Supplier<Stream<SimpleConnectionShape>> outgoingsOpt = () -> scpOpt
        .map(SimpleConnectionPointShape::getOutgoingConnections)
        .map(List::stream).orElseGet(Stream::empty)
        .filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast);
    Optional<SimpleConnectionShape> firstOutgoing = outgoingsOpt.get()
        .findFirst();

    Optional<Point> bendpointInsteadOfConnectionPoint = scpOpt
        .map(scp -> Graphiti.getCreateService().createPoint(
            scp.getX() + (scp.getPointSize() >> 1),
            scp.getY() + (scp.getPointSize() >> 1)));

    Optional<FlowTargetShape> targetAnchor = firstOutgoing
        .map(SimpleConnectionShape::getTarget);
    incommingOpt.ifPresent(i -> targetAnchor.ifPresent(i::setTarget));
    incommingOpt.ifPresent(
        i -> i.getBendpoints().addAll(bendpointInsteadOfConnectionPoint.stream()
            .collect(Collectors.toList())));
    incommingOpt.ifPresent(i -> i.getBendpoints()
        .addAll(firstOutgoing.map(SimpleConnectionShape::getBendpoints)
            .map(EList::stream).orElseGet(Stream::empty)
            .collect(Collectors.toList())));

    outgoingsOpt.get().skip(1).collect(Collectors.toList())
        .forEach((SimpleConnectionShape o) -> {
          DeleteContext deleteCtx = new DeleteContext(o);
          deleteCtx.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
          IDeleteFeature deleteFeature = getFeatureProvider()
              .getDeleteFeature(deleteCtx);
          deleteFeature.delete(deleteCtx);
        });

    firstOutgoing.ifPresent((SimpleConnectionShape o) -> {
      RemoveContext removeCtx = new RemoveContext(o);
      getFeatureProvider().getRemoveFeature(removeCtx).remove(removeCtx);
    });
  }

}
