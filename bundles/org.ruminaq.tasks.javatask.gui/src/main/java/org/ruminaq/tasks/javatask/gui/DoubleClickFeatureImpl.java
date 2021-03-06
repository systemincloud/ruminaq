/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Collections;
import java.util.List;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.DoubleClickFeatureExtension;
import org.ruminaq.tasks.javatask.gui.features.DoubleClickFeature;

/**
 * Service DoubleClickFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=10" })
public class DoubleClickFeatureImpl implements DoubleClickFeatureExtension {

  @Override
  public List<Class<? extends ICustomFeature>> getFeatures() {
    return Collections.singletonList(DoubleClickFeature.class);
  }
}
