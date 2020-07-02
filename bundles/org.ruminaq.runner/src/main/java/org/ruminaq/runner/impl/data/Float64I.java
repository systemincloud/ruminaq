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

public class Float64I extends NumericI {

  private static final long serialVersionUID = 1L;

  private double[] values;

  private Float64I() {
    super(new LinkedList<Integer>());
  }

  public Float64I(double value) {
    super(new LinkedList<Integer>() {
      private static final long serialVersionUID = 1L;
      {
        add(1);
      }
    });
    values = new double[] { value };
  }

  public Float64I(double[] values, List<Integer> dims) {
    this(values, dims, false);
  }

  public Float64I(double[] values, List<Integer> dims, boolean copy) {
    super(dims);
    if (copy) {
      this.values = new double[values.length];
      System.arraycopy(values, 0, this.values, 0, values.length);
    } else
      this.values = values;
  }

  public Float64I(RemoteData data) {
    super(data.getDims());
    try {
      DoubleBuffer fb = ByteBuffer.wrap(data.getBuf()).asDoubleBuffer();
      this.values = new double[fb.limit()];
      fb.get(this.values);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public RemoteData getRemoteData() {
    RemoteData rd = new RemoteData();
    rd.dims = this.dims;
    rd.type = "Float64";

    ByteBuffer byteBuffer = ByteBuffer.allocate(this.values.length << 3);
    DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
    doubleBuffer.put(this.values);
    rd.buf = byteBuffer;
    return rd;
  }

  public double[] getValues() {
    return values;
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
      return toComplex64I();
    else if (ControlI.class.isAssignableFrom(clazz))
      return toControlI();
    else if (DecimalI.class.isAssignableFrom(clazz))
      return toDecimalI();
    else if (Float32I.class.isAssignableFrom(clazz))
      return toFloat32I();
    else if (Float64I.class.isAssignableFrom(clazz))
      return this;
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
      values[i] = this.values[i] != 0;
    return new BoolI(values, dims);
  }

  public DataI toComplex32I() {
    float[] realValues = new float[nElements];
    float[] imagValues = new float[nElements];
    for (int i = 0; i < nElements; i++)
      realValues[i] = (float) this.values[i];
    return new Complex32I(realValues, imagValues, dims);
  }

  public DataI toComplex64I() {
    double[] realValues = new double[nElements];
    double[] imagValues = new double[nElements];
    for (int i = 0; i < nElements; i++)
      realValues[i] = this.values[i];
    return new Complex64I(realValues, imagValues, dims);
  }

  public DataI toControlI() {
    return new ControlI();
  }

  public DataI toDecimalI() {
    BigDecimal[] values = new BigDecimal[nElements];
    for (int i = 0; i < nElements; i++)
      values[i] = new BigDecimal(this.values[i]);
    return new DecimalI(values, dims);
  }

  public DataI toFloat32I() {
    float[] fValues = new float[nElements];
    for (int i = 0; i < nElements; i++)
      fValues[i] = (float) this.values[i];
    return new Float32I(fValues, dims);
  }

  public DataI toInt32I() {
    int[] values = new int[nElements];
    for (int i = 0; i < nElements; i++)
      values[i] = (int) this.values[i];
    return new Int32I(values, dims);
  }

  public DataI toInt64I() {
    long[] values = new long[nElements];
    for (int i = 0; i < nElements; i++)
      values[i] = (long) this.values[i];
    return new Int64I(values, dims);
  }

  public DataI toRawI() {
    byte[] values = new byte[nElements * 8];
    ByteBuffer.wrap(values).asDoubleBuffer().put(this.values);
    return new RawI(values, dims);
  }

  public DataI toTextI() {
    String[] sValues = new String[nElements];
    for (int i = 0; i < nElements; i++)
      sValues[i] = Double.toString(this.values[i]);
    return new TextI(sValues, dims);
  }
}
