/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.Collections;
import java.util.List;

import org.eclipse.graphiti.features.IPasteFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.PasteFeatureExtension;

/**
 * Common paste feature.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class PasteFeatures implements PasteFeatureExtension {

  @Override
  public List<Class<? extends IPasteFeature>> getFeatures() {
    return Collections.singletonList(PasteElementFeature.class);
  }
}
