/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.gate.gui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.IAddFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.AddFeatureExtension;
import org.ruminaq.tasks.gate.gui.and.AddAndFeature;
import org.ruminaq.tasks.gate.gui.not.AddNotFeature;
import org.ruminaq.tasks.gate.gui.or.AddOrFeature;
import org.ruminaq.tasks.gate.gui.xor.AddXorFeature;

/**
 * Service AddFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=10" })
public class AddFeatureImpl implements AddFeatureExtension {

  @Override
  public List<Class<? extends IAddFeature>> getFeatures() {
    return Arrays.asList(AddAndFeature.class, AddNotFeature.class,
        AddOrFeature.class, AddXorFeature.class);
  }
}
