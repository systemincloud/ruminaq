/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.PasteElementFeatureExtension;
import org.ruminaq.gui.model.diagram.RuminaqShape;

/**
 * Service PasteElementFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class PasteElementFeatures implements PasteElementFeatureExtension {

  @Override
  public List<Class<? extends RuminaqShapePasteFeature<? extends RuminaqShape>>> getFeatures() {
    return Arrays.asList(PasteInputPortFeature.class,
        PasteOutputPortFeature.class);
  }
}
