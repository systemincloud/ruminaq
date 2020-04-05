/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.port;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.impl.ChopboxAnchorImpl;
import org.ruminaq.gui.model.diagram.OutputPortShape;
import org.ruminaq.gui.model.diagram.impl.NoResource;

/**
 * Anchor of OutputPortShape.
 *
 * @author Marek Jagielski
 */
public class OutputPortAnchor extends ChopboxAnchorImpl {

  private OutputPortShape shape;

  /**
   * Anchor of OutputPortShape.
   * 
   * @param shape parent OutputPortShape
   */
  public OutputPortAnchor(OutputPortShape shape) {
    this.shape = shape;
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }

  @Override
  public AnchorContainer getParent() {
    return shape;
  }

  @Override
  public EList<Connection> getIncomingConnections() {
    return new BasicEList<>(shape.getIncomingConnections());
  }

}
