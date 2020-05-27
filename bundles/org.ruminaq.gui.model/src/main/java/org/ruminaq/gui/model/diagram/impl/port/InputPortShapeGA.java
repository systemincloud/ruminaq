/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.port;

import java.util.Optional;

import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.ruminaq.gui.model.diagram.InputPortShape;
import org.ruminaq.model.ruminaq.InputPort;

/**
 * GraphicsAlgorithm for InputPort.
 *
 * @author Marek Jagielski
 */
public class InputPortShapeGA extends PortShapeGA {

  private InputPortShape shape;

  /**
   * GraphicsAlgorithm for InputPort.
   * 
   * @param shape parent InputPortShape
   */
  public InputPortShapeGA(InputPortShape shape) {
    super(shape);
    this.shape = shape;
  }

  @Override
  public LineStyle getLineStyle() {
    if (Optional.of(shape).map(InputPortShape::getModelObject)
        .filter(InputPort.class::isInstance).map(InputPort.class::cast)
        .filter(InputPort::isAsynchronous).isPresent()) {
      return LineStyle.DOT;
    } else {
      return LineStyle.SOLID;
    }
  }

  @Override
  public Integer getLineWidth() {
    return 1;
  }
}
