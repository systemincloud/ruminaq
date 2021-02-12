/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.simpleconnection;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.impl.PolylineImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.ruminaq.gui.model.diagram.impl.Colors;
import org.ruminaq.gui.model.diagram.impl.NoResource;

/**
 * GraphicsAlgorithm for SimpleConnection.
 *
 * @author Marek Jagielski
 */
public class SimpleConnectionShapeGA extends PolylineImpl {
  
  @Override
  public Integer getLineWidth() {
    return 1;
  }
  
  @Override
  public Color getForeground() {
    return Colors.BLACK;
  }
  
  @Override
  public Resource eResource() {
    return new NoResource();
  }
}
