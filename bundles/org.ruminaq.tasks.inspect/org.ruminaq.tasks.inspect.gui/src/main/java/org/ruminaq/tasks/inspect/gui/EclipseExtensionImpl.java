/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.inspect.gui;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.inspect.model.inspect.InspectPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
    InspectPackage.eINSTANCE.getClass();
  }
}
