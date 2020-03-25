/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.reconnection;

import java.util.Optional;
import java.util.function.Predicate;

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

  @Override
  public boolean canReconnect(IReconnectionContext context) {
    Optional<BaseElement> oldMo = Optional.of(context)
        .map(IReconnectionContext::getOldAnchor).map(Anchor::getParent)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject);
    Optional<BaseElement> newMo = Optional.of(context)
        .map(IReconnectionContext::getNewAnchor).map(Anchor::getParent)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject);
    Optional<Anchor> targetShape = Optional.of(context)
        .map(IReconnectionContext::getNewAnchor);
    return targetShape.map(Anchor::getIncomingConnections)
        .filter(Predicate.not(EList<Connection>::isEmpty)).isEmpty()
        && ((oldMo.filter(FlowSource.class::isInstance).isPresent()
            && newMo.filter(FlowSource.class::isInstance).isPresent())
            || (oldMo.filter(FlowTarget.class::isInstance).isPresent()
                && newMo.filter(FlowTarget.class::isInstance).isPresent()));
  }

  @Override
  public boolean canStartReconnect(IReconnectionContext context) {
    return Optional.of(context).map(IReconnectionContext::getOldAnchor)
        .map(Anchor::getParent)
        .filter(SimpleConnectionPointShape.class::isInstance).isEmpty();
  }

}
