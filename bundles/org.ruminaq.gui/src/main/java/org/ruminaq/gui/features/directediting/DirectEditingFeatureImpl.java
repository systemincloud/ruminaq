/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.directediting;

import java.util.Collections;
import java.util.List;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.DirectEditingFeatureExtension;

/**
 * Service DirectEditingFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class DirectEditingFeatureImpl implements DirectEditingFeatureExtension {

  @Override
  public List<Class<? extends IDirectEditingFeature>> getFeatures() {
    return Collections.singletonList(DirectEditLabelFeature.class);
  }
}
