/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.inspect.gui;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.gui.features.doubleclick.DoubleClickBaseElementFeature;
import org.ruminaq.tasks.Windows;
import org.ruminaq.tasks.inspect.model.inspect.Inspect;

public class DoubleClickFeature extends DoubleClickBaseElementFeature {

  public DoubleClickFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public void execute(ICustomContext context) {
    Inspect bo = null;
    for (Object o : Graphiti.getLinkService()
        .getAllBusinessObjectsForLinkedPictogramElement(
            context.getPictogramElements()[0]))
      if (o instanceof Inspect) {
        bo = (Inspect) o;
        break;
      }
    if (bo == null)
      return;

    Windows.INSTANCE.connectWindow(InspectWindow.class, bo);
  }
}
