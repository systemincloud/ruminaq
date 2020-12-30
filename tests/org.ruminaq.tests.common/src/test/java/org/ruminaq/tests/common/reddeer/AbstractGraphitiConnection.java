/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common.reddeer;

import org.eclipse.graphiti.ui.internal.parts.ConnectionEditPart;
import org.eclipse.reddeer.gef.impl.connection.AbstractConnection;
import org.hamcrest.Matcher;

public class AbstractGraphitiConnection extends AbstractConnection {

  /**
   * Constructs graphiti edit part which fulfills a given matcher.
   * 
   * @param matcher matcher to match edit part
   * @param index index of edit part
   */
  public AbstractGraphitiConnection(Matcher<ConnectionEditPart> matcher, int index) {
    super(matcher, index);
  }

}
