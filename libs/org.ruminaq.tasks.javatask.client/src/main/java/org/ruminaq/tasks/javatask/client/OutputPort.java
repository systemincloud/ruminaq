/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client;

import org.ruminaq.tasks.javatask.client.data.Data;

/**
 *
 * @author Marek Jagielski
 */
public class OutputPort {

  private JavaTaskListener jtListener;

  private String name;

  public OutputPort(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void putData(Data data) {
    jtListener.putData(this, data, false);
  }

  public void putData(Data data, boolean copy) {
    jtListener.putData(this, data, copy);
  }
}
