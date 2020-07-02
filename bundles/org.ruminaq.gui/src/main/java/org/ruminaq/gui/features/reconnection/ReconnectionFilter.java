/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.reconnection;

import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.ruminaq.gui.features.FeaturePredicate;

public class ReconnectionFilter<V extends Connection>
    implements FeaturePredicate<IContext> {

  private Class<V> connectionClass;

  public ReconnectionFilter(Class<V> connectionClass) {
    this.connectionClass = connectionClass;
  }

  @Override
  public boolean test(IContext context, IFeatureProvider fp) {
    return Optional.of(context).filter(IReconnectionContext.class::isInstance)
        .map(IReconnectionContext.class::cast)
        .map(IReconnectionContext::getConnection)
        .filter(connectionClass::isInstance).isPresent();
  }

}
