/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common.reddeer;

import java.util.Optional;
import org.eclipse.graphiti.mm.pictograms.Connection;

public class WithShapeGraphitiConnection extends AbstractGraphitiConnection {

  /**
   * Finds a graphiti edit part with shape of given class.
   *
   * @param clazz
   */
  public WithShapeGraphitiConnection(Class<?> clazz) {
    this(clazz, 0);
  }

  /**
   * Finds a graphiti edit part with shape of given class at the specified
   * index.
   *
   * @param clazz
   * @param index
   */
  public WithShapeGraphitiConnection(Class<?> clazz, int index) {
    super(new IsConnectionWithShapeOfType(clazz), index);
  }

  public Optional<Connection> getConnection() {
    return IsConnectionWithShapeOfType.getPictogramElement(connection)
        .filter(Connection.class::isInstance).map(Connection.class::cast);
  }

}
