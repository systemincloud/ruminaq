/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.data;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.runner.thrift.RemoteData;

public class Complex64I extends NumericI {

  private static final long serialVersionUID = 1L;

  private double[] real;
  private double[] imag;

  private Complex64I() {
    super(new LinkedList<Integer>());
  }

  public Complex64I(double real, double imag) {
    super(new LinkedList<Integer>() {
      private static final long serialVersionUID = 1L;
      {
        add(1);
      }
    });
    this.real = new double[] { real };
    this.imag = new double[] { imag };
  }

  public Complex64I(double[] real, double[] imag, List<Integer> dims) {
    this(real, imag, dims, false);
  }

  public Complex64I(double[] real, double[] imag, List<Integer> dims,
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

  public Complex64I(RemoteData data) {
    super(data.getDims());
    try {
      DoubleBuffer fb = ByteBuffer.wrap(data.getBuf()).asDoubleBuffer();
      double[] array = new double[fb.limit()];
      fb.get(array);

      this.real = new double[array.length >> 1];
      this.imag = new double[array.length >> 1];

      for (int i = 0; i < array.length >> 1; i++)
        this.real[i] = array[i];

      for (int i = 0; i < array.length >> 1; i++)
        this.imag[i] = array[i + (array.length >> 1)];
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public RemoteData getRemoteData() {
    RemoteData rd = new RemoteData();
    rd.dims = this.dims;
    rd.type = "Complex64";
    double[] array = new double[this.real.length + this.imag.length];

    for (int i = 0; i < real.length; i++)
      array[i] = this.real[i];

    for (int i = 0; i < imag.length; i++)
      array[i + imag.length] = this.imag[i];

    ByteBuffer byteBuffer = ByteBuffer.allocate(array.length << 3);
    DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
    doubleBuffer.put(array);
    rd.buf = byteBuffer;
    return rd;
  }

  public double[] getRealValues() {
    return real;
  }

  public double[] getImagValues() {
    return imag;
  }

  @Override
  public String toString() {
    return toTextI().toString();
  }

  @Override
  public String toShortString() {
    return toTextI().toShortString();
  }

  @Override
  protected DataI to(Class<? extends DataI> clazz) {
    if (BoolI.class.isAssignableFrom(clazz))
      return toBoolI();
    else if (Complex32I.class.isAssignableFrom(clazz))
      return toComplex32I();
    else if (Complex64I.class.isAssignableFrom(clazz))
      return this;
    else if (ControlI.class.isAssignableFrom(clazz))
      return toControlI();
    else if (DecimalI.class.isAssignableFrom(clazz))
      return toDecimalI();
    else if (Float32I.class.isAssignableFrom(clazz))
      return toFloat32I();
    else if (Float64I.class.isAssignableFrom(clazz))
      return toFloat64I();
    else if (Int32I.class.isAssignableFrom(clazz))
      return toInt32I();
    else if (Int64I.class.isAssignableFrom(clazz))
      return toInt64I();
    else if (RawI.class.isAssignableFrom(clazz))
      return toRawI();
    else if (TextI.class.isAssignableFrom(clazz))
      return toTextI();
    return from(clazz);
  }

  //
  // Converters
  //

  public DataI toBoolI() {
    boolean[] values = new boolean[nElements];
    for (int i = 0; i < nElements; i++)
      values[i] = this.real[i] != 0 || this.imag[i] != 0;
    return new BoolI(values, dims);
  }

  public DataI toComplex32I() {
    float[] realValues = new float[nElements];
    float[] imagValues = new float[nElements];
    for (int i = 0; i < nElements; i++)
      realValues[i] = (float) this.real[i];
    for (int i = 0; i < nElements; i++)
      imagValues[i] = (float) this.imag[i];
    return new Complex32I(realValues, imagValues, dims);
  }

  public DataI toControlI() {
    return new ControlI();
  }

  public DataI toDecimalI() {
    BigDecimal[] values = new BigDecimal[nElements];
    for (int i = 0; i < nElements; i++)
      values[i] = new BigDecimal(this.real[i]);
    return new DecimalI(values, dims);
  }

  public DataI toFloat32I() {
    float[] values = new float[nElements];
    for (int i = 0; i < nElements; i++)
      values[i] = (float) this.real[i];
    return new Float32I(values, dims);
  }

  public DataI toFloat64I() {
    double[] values = new double[nElements];
    for (int i = 0; i < nElements; i++)
      values[i] = this.real[i];
    return new Float64I(values, dims);
  }

  public DataI toInt32I() {
    int[] values = new int[nElements];
    for (int i = 0; i < nElements; i++)
      values[i] = (int) this.real[i];
    return new Int32I(values, dims);
  }

  public DataI toInt64I() {
    long[] values = new long[nElements];
    for (int i = 0; i < nElements; i++)
      values[i] = (long) this.real[i];
    return new Int64I(values, dims);
  }

  public DataI toRawI() {
    byte[] values = new byte[2 * nElements * 8];
    byte[] realBytes = new byte[nElements * 8];
    byte[] imagBytes = new byte[nElements * 8];
    ByteBuffer.wrap(realBytes).asDoubleBuffer().put(this.real);
    ByteBuffer.wrap(imagBytes).asDoubleBuffer().put(this.imag);
    System.arraycopy(realBytes, 0, values, 0, realBytes.length);
    System.arraycopy(imagBytes, 0, values, imagBytes.length, imagBytes.length);
    return new RawI(values, dims);
  }

  public DataI toTextI() {
    String[] values = new String[nElements];
    for (int i = 0; i < nElements; i++) {
      String sign = this.imag[i] < 0 ? " - " : " + ";
      values[i] = Double.toString(this.real[i]) + sign
          + Double.toString(Math.abs(this.imag[i])) + "i";
    }
    return new TextI(values, dims);
  }
}
