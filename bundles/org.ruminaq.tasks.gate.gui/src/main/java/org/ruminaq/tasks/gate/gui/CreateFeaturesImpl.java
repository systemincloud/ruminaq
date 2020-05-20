/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.gate.gui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CreateFeaturesExtension;
import org.ruminaq.tasks.gate.gui.and.CreateAndFeature;
import org.ruminaq.tasks.gate.gui.not.CreateNotFeature;
import org.ruminaq.tasks.gate.gui.or.CreateOrFeature;
import org.ruminaq.tasks.gate.gui.xor.CreateXorFeature;

/**
 * Service CreateFeaturesExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=10" })
public class CreateFeaturesImpl implements CreateFeaturesExtension {

  @Override
  public List<Class<? extends ICreateFeature>> getFeatures() {
    return Arrays.asList(CreateAndFeature.class, CreateNotFeature.class,
        CreateOrFeature.class, CreateXorFeature.class);
  }
}
