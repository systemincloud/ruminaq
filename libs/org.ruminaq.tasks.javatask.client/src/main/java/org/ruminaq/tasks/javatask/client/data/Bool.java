/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client.data;

import java.util.List;

/**
 *
 * @author Marek Jagielski
 */
public class Bool extends Data {

  private boolean[] values;

  public Bool(boolean value) {
    super(1);
    values = new boolean[] { value };
  }

  public Bool(List<Integer> dims, boolean[] values) {
    this(dims, values, false);
  }

  public Bool(List<Integer> dims, boolean[] values, boolean copy) {
    super(dims);
    if (copy) {
      this.values = new boolean[values.length];
      System.arraycopy(values, 0, this.values, 0, values.length);
    } else {
      this.values = values;
    }
  }

  public boolean[] getValues() {
    return values;
  }

  public boolean getValue() {
    return values[0];
  }
}
