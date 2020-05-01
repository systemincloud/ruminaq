/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.ruminaq.gui.model.diagram.InternalOutputPortShape;
import org.ruminaq.gui.model.diagram.impl.NoResource;

/**
 * GraphicsAlgorithm for InternalOutputPort.
 *
 * @author Marek Jagielski
 */
public class InternalOutputPortShapeGA extends InternalPortShapeGA {

  private InternalOutputPortShape shape;

  /**
   * GraphicsAlgorithm for InternalOutputPort.
   * 
   * @param shape parent InternalOutputPortShape
   */
  public InternalOutputPortShapeGA(InternalOutputPortShape shape) {
    this.shape = shape;
  }

  @Override
  public EList<GraphicsAlgorithm> getGraphicsAlgorithmChildren() {
    return ECollections.emptyEList();
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }
}
