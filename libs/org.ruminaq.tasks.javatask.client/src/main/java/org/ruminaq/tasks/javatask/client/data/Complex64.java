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
public class Complex64 extends Data {

  private double[] real;
  private double[] imag;

  public Complex64(double real, double imag) {
    super(1);
    this.real = new double[] { real };
    this.imag = new double[] { imag };
  }

  public Complex64(List<Integer> dims, double[] real, double[] imag) {
    this(dims, real, imag, false);
  }

  public Complex64(List<Integer> dims, double[] real, double[] imag,
      boolean copy) {
    super(dims);
    if (copy) {
      this.real = new double[real.length];
      this.imag = new double[imag.length];
      System.arraycopy(real, 0, this.real, 0, real.length);
      System.arraycopy(imag, 0, this.imag, 0, imag.length);
    } else {
      this.real = real;
      this.imag = imag;
    }
  }

  public double[] getRealValues() {
    return real;
  }

  public double[] getImagValues() {
    return imag;
  }

  public double getRealValue() {
    return real[0];
  }

  public double getImagValue() {
    return imag[0];
  }
}
