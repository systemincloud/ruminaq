/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tests.common.reddeer;

import org.eclipse.reddeer.graphiti.impl.graphitieditpart.AbstractGraphitiEditPart;

public class WithLabelAssociated extends AbstractGraphitiEditPart {

  public WithLabelAssociated(String text) {
    this(text, 0);
  }

  public WithLabelAssociated(String text, int index) {
    super(new HasLabelWithText(text), index);
  }

}
