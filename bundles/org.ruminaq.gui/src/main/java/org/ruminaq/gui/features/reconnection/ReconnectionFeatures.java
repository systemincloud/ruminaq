/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.reconnection;

import java.util.Collections;
import java.util.List;

import org.eclipse.graphiti.features.IReconnectionFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ReconnectionFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class ReconnectionFeatures implements ReconnectionFeatureExtension {

  @Override
  public List<Class<? extends IReconnectionFeature>> getFeatures() {
    return Collections.singletonList(ReconnectionSimpleConnectionFeature.class);
  }
}
