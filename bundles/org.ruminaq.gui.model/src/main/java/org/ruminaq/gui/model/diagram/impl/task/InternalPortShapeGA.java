/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.impl.RoundedRectangleImpl;
import org.ruminaq.gui.model.diagram.impl.NoResource;

public class InternalPortShapeGA extends RoundedRectangleImpl {

  @Override
  public Resource eResource() {
    return new NoResource();
  }
}
