/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import java.util.Optional;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.ruminaq.gui.model.diagram.InternalInputPortShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.impl.NoResource;
import org.ruminaq.model.ruminaq.InternalInputPort;

/**
 * GraphicsAlgorithm for InternalOutputPort.
 *
 * @author Marek Jagielski
 */
public class InternalInputPortShapeGA extends InternalPortShapeGA {

  /**
   * GraphicsAlgorithm for InternalOutputPort.
   *
   * @param shape parent InternalOutputPortShape
   */
  public InternalInputPortShapeGA(InternalInputPortShape shape) {
    super(shape);
  }

  @Override
  public LineStyle getLineStyle() {
    if (Optional.of(shape).map(RuminaqShape::getModelObject)
        .filter(InternalInputPort.class::isInstance)
        .map(InternalInputPort.class::cast)
        .filter(InternalInputPort::isAsynchronous).isPresent()) {
      return LineStyle.DOT;
    } else {
      return LineStyle.SOLID;
    }
  }

  @Override
  public Resource eResource() {
    return new NoResource();
  }
}
