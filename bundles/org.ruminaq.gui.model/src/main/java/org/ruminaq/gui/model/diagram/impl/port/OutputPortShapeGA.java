/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.port;

import org.ruminaq.gui.model.diagram.OutputPortShape;

/**
 * GraphicsAlgorithm for OutputPort.
 *
 * @author Marek Jagielski
 */
public class OutputPortShapeGA extends PortShapeGA {

  /**
   * GraphicsAlgorithm for OutputPort.
   *
   * @param shape parent OutputPortShape
   */
  public OutputPortShapeGA(OutputPortShape shape) {
    super(shape);
  }

  @Override
  public Integer getLineWidth() {
    return 1 << 1;
  }
}
