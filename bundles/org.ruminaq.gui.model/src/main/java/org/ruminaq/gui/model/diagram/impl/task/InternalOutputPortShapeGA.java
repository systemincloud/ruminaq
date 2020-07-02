/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.gui.model.diagram.impl.task;

import org.eclipse.emf.ecore.resource.Resource;
import org.ruminaq.gui.model.diagram.InternalOutputPortShape;
import org.ruminaq.gui.model.diagram.impl.NoResource;

/**
 * GraphicsAlgorithm for InternalOutputPort.
 *
 * @author Marek Jagielski
 */
public class InternalOutputPortShapeGA extends InternalPortShapeGA {

  private static final int WIDTH = 2;

  /**
   * GraphicsAlgorithm for InternalOutputPort.
   * 
   * @param shape parent InternalOutputPortShape
   */
  public InternalOutputPortShapeGA(InternalOutputPortShape shape) {
    super(shape);
  }

  @Override
  public Integer getLineWidth() {
    return WIDTH;
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }
}
