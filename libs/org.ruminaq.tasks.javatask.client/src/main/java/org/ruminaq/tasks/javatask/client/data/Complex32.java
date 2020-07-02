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
public class Complex32 extends Data {

  private float[] real;
  private float[] imag;

  public Complex32(float real, float imag) {
    super(1);
    this.real = new float[] { real };
    this.imag = new float[] { imag };
  }

  public Complex32(List<Integer> dims, float[] real, float[] imag) {
    this(dims, real, imag, false);
  }

  public Complex32(List<Integer> dims, float[] real, float[] imag,
      boolean copy) {
    super(dims);
    if (copy) {
      this.real = new float[real.length];
      this.imag = new float[imag.length];
      System.arraycopy(real, 0, this.real, 0, real.length);
      System.arraycopy(imag, 0, this.imag, 0, imag.length);
    } else {
      this.real = real;
      this.imag = imag;
    }
  }

  public float[] getRealValues() {
    return real;
  }

  public float[] getImagValues() {
    return imag;
  }

  public float getRealValue() {
    return real[0];
  }

  public float getImagValue() {
    return imag[0];
  }
}
