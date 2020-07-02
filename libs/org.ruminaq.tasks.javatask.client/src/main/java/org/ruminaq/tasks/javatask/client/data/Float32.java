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
public class Float32 extends Data {

  private float[] values;

  public Float32(float value) {
    super(1);
    values = new float[] { value };
  }

  public Float32(List<Integer> dims, float[] values) {
    this(dims, values, false);
  }

  public Float32(List<Integer> dims, float[] values, boolean copy) {
    super(dims);
    if (copy) {
      this.values = new float[values.length];
      System.arraycopy(values, 0, this.values, 0, values.length);
    } else
      this.values = values;
  }

  public float[] getValues() {
    return values;
  }

  public float getValue() {
    return values[0];
  }
}
