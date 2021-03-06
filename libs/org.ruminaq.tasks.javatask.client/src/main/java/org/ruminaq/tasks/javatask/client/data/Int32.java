/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client.data;

import java.util.List;

/**
 *
 * @author Marek Jagielski
 */
public class Int32 extends Data {

  private int[] values;

  public Int32(int value) {
    super(1);
    values = new int[] { value };
  }

  public Int32(List<Integer> dims, int[] values) {
    this(dims, values, false);
  }

  public Int32(List<Integer> dims, int[] values, boolean copy) {
    super(dims);
    if (copy) {
      this.values = new int[values.length];
      System.arraycopy(values, 0, this.values, 0, values.length);
    } else
      this.values = values;
  }

  public int[] getValues() {
    return values;
  }

  public int getValue() {
    return values[0];
  }
}
