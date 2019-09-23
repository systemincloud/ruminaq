/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client;

import org.ruminaq.tasks.javatask.client.data.Data;

/**
 *
 * @author Marek Jagielski
 */
public class InputPort {

  private JavaTaskListener jtListener;

  private String name;

  public InputPort(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public <T extends Data> T getData(Class<T> clazz) {
    return jtListener.getData(this, clazz);
  }

  public void cleanQueue() {
    jtListener.cleanQueue(this);
  }
}
