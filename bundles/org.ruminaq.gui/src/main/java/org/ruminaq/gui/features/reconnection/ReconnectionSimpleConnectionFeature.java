/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.reconnection;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.reconnection.ReconnectionSimpleConnectionFeature.Filter;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.FlowSource;
import org.ruminaq.model.ruminaq.FlowTarget;
import org.ruminaq.model.ruminaq.SimpleConnection;

/**
 * IReconnectionFeature for SimpleConnection.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class ReconnectionSimpleConnectionFeature
    extends DefaultReconnectionFeature {

  public static class Filter extends ReconnectionFilter<SimpleConnectionShape> {
    public Filter() {
      super(SimpleConnectionShape.class);
    }
  }

  public ReconnectionSimpleConnectionFeature(IFeatureProvider fp) {
    super(fp);
  }

  private static Optional<BaseElement> modelFromContext(
      IReconnectionContext context,
      Function<IReconnectionContext, Anchor> anchorSelector) {
    return Optional.of(context).map(anchorSelector).map(Anchor::getParent)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject);
  }

  private static Optional<SimpleConnection> modelFromContext(
      IReconnectionContext context) {
    return Optional.of(context).map(IReconnectionContext::getConnection)
        .filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast)
        .map(SimpleConnectionShape::getModelObject).map(List::stream)
        .orElseGet(Stream::empty).findFirst()
        .filter(SimpleConnection.class::isInstance)
        .map(SimpleConnection.class::cast);
  }

  private static void changeSource(IReconnectionContext context) {
    modelFromContext(context, IReconnectionContext::getNewAnchor)
        .filter(FlowSource.class::isInstance).map(FlowSource.class::cast)
        .ifPresent(
            m -> modelFromContext(context).ifPresent(sc -> sc.setSourceRef(m)));
  }

  private static void changeTarget(IReconnectionContext context) {
    modelFromContext(context, IReconnectionContext::getNewAnchor)
        .filter(FlowTarget.class::isInstance).map(FlowTarget.class::cast)
        .ifPresent(
            m -> modelFromContext(context).ifPresent(sc -> sc.setTargetRef(m)));
  }

  /**
   * FlowSource can be changed to FlowSource, FlowTarget can be changed to
   * FlowTarget.
   */
  @Override
  public boolean canReconnect(IReconnectionContext context) {
    Optional<BaseElement> oldMo = modelFromContext(context,
        IReconnectionContext::getOldAnchor);
    Optional<BaseElement> newMo = modelFromContext(context,
        IReconnectionContext::getNewAnchor);
    Optional<Anchor> targetShape = Optional.of(context)
        .map(IReconnectionContext::getNewAnchor);
    return targetShape.map(Anchor::getIncomingConnections)
        .filter(Predicate.not(EList<Connection>::isEmpty)).isEmpty()
        && ((oldMo.filter(FlowSource.class::isInstance).isPresent()
            && newMo.filter(FlowSource.class::isInstance).isPresent())
            || (oldMo.filter(FlowTarget.class::isInstance).isPresent()
                && newMo.filter(FlowTarget.class::isInstance).isPresent()));
  }

  /**
   * Can't drag connection on ConnectionPoint.
   */
  @Override
  public boolean canStartReconnect(IReconnectionContext context) {
    return Optional.of(context).map(IReconnectionContext::getOldAnchor)
        .map(Anchor::getParent)
        .filter(SimpleConnectionPointShape.class::isInstance).isEmpty();
  }

  @Override
  public void preReconnect(IReconnectionContext context) {
    changeSource(context);
    changeTarget(context);

  }
}
