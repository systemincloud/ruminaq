/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.gate.gui;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;

public abstract class AddGateFeature extends AbstractAddTaskFeature {

  private static final int SIZE = 50;
  
  public AddGateFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected int getHeight() {
    return SIZE;
  }

  @Override
  protected int getWidth() {
    return getHeight();
  }
}
