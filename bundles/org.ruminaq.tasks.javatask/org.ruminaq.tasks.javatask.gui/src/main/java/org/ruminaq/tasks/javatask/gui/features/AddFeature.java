/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.add.AddTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.javatask.gui.Images;
import org.ruminaq.tasks.javatask.gui.Port;

public class AddFeature extends AddTaskFeature {

  public static String NOT_CHOSEN = "???";

  public AddFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected boolean useIconInsideShape() {
    return true;
  }

  @Override
  protected String getInsideIconId() {
    return Images.K.IMG_JAVATASK_DIAGRAM.name();
  }

  @Override
  protected String getInsideIconDesc() {
    return NOT_CHOSEN;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }
}
