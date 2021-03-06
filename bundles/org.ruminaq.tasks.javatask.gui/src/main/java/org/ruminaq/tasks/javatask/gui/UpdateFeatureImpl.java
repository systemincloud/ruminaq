/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Collections;
import java.util.List;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.UpdateFeatureExtension;
import org.ruminaq.tasks.javatask.gui.features.UpdateFeature;

/**
 * Service UpdateFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class UpdateFeatureImpl implements UpdateFeatureExtension {

  @Override
  public List<Class<? extends IUpdateFeature>> getFeatures() {
    return Collections.singletonList(UpdateFeature.class);
  }
}
