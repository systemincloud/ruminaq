/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.debug.events.debugger;

import java.io.Serializable;

public class NewOutputPort implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  public String getId() {
    return id;
  }

  public NewOutputPort(String id) {
    this.id = id;
  }
}
