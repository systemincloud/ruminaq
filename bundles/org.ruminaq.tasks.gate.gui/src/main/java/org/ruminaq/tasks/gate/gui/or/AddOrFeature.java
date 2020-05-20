/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.gate.gui.or;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.tasks.gate.gui.AddGateFeature;
import org.ruminaq.tasks.gate.gui.Images;

public class AddOrFeature extends AddGateFeature {

  public AddOrFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected String getInsideIconId() {
    return Images.IMG_OR_DIAGRAM;
  }
}
