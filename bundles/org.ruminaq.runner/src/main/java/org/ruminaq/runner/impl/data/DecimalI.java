package org.ruminaq.runner.impl.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.runner.thrift.RemoteData;

public class DecimalI extends NumericI {

  private static final long serialVersionUID = 1L;

  private List<BigDecimal> values = new LinkedList<>();

  private DecimalI() {
    super(new LinkedList<Integer>());
  }

  public DecimalI(BigDecimal value) {
    super(new LinkedList<Integer>() {
      private static final long serialVersionUID = 1L;
      {
        add(1);
      }
    });
    this.values.add(value);
  }

  public DecimalI(List<BigDecimal> values, List<Integer> dims) {
    super(dims);
    this.values.addAll(values);
  }

  public DecimalI(BigDecimal[] values, List<Integer> dims) {
    super(dims);
    this.values.addAll(Arrays.asList(values));
  }

  public DecimalI(RemoteData data) {
    super(data.getDims());
    for (String s : new String(data.getBuf()).split(" "))
      if (!"".equals(s))
        this.values.add(new BigDecimal(s));
  }

  @Override
  public RemoteData getRemoteData() {
    RemoteData rd = new RemoteData();
    rd.dims = this.dims;
    rd.type = "Decimal";
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    for (BigDecimal v : this.values) {
      BigInteger bi = v.unscaledValue().abs();
      byte[] bv = bi.toByteArray();
      try {
        byteStream.write(ByteBuffer.allocate(4).putInt(v.signum()).array());
        byteStream.write(ByteBuffer.allocate(4).putInt(v.precision()).array());
        byteStream.write(ByteBuffer.allocate(4).putInt(v.scale()).array());
        int missingbytes = bv.length % 4;
        if (missingbytes != 0)
          missingbytes = 4 - missingbytes;
        byteStream.write(
            ByteBuffer.allocate(4).putInt(bv.length + missingbytes).array());
        for (int i = 0; i < missingbytes; i++)
          byteStream.write((byte) 0);
        byteStream.write(bv);
      } catch (IOException e) {
      }
    }
    rd.buf = ByteBuffer.wrap(byteStream.toByteArray());

    return rd;
  }

  public List<BigDecimal> getValues() {
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
      return this;
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
    Iterator<BigDecimal> it = this.values.iterator();
    for (int i = 0; i < nElements; i++)
      values[i] = it.next().compareTo(BigDecimal.ZERO) != 0;
    return new BoolI(values, dims);
  }

  public DataI toComplex32I() {
    float[] realValues = new float[nElements];
    float[] imagValues = new float[nElements];
    Iterator<BigDecimal> it = this.values.iterator();
    for (int i = 0; i < nElements; i++)
      realValues[i] = it.next().floatValue();
    return new Complex32I(realValues, imagValues, dims);
  }

  public DataI toComplex64I() {
    double[] realValues = new double[nElements];
    double[] imagValues = new double[nElements];
    Iterator<BigDecimal> it = this.values.iterator();
    for (int i = 0; i < nElements; i++)
      realValues[i] = it.next().doubleValue();
    return new Complex64I(realValues, imagValues, dims);
  }

  public DataI toControlI() {
    return new ControlI();
  }

  public DataI toFloat32I() {
    float[] values = new float[nElements];
    Iterator<BigDecimal> it = this.values.iterator();
    for (int i = 0; i < nElements; i++)
      values[i] = it.next().floatValue();
    return new Float32I(values, dims);
  }

  public DataI toFloat64I() {
    double[] values = new double[nElements];
    Iterator<BigDecimal> it = this.values.iterator();
    for (int i = 0; i < nElements; i++)
      values[i] = it.next().doubleValue();
    return new Float64I(values, dims);
  }

  public DataI toInt32I() {
    int[] values = new int[nElements];
    Iterator<BigDecimal> it = this.values.iterator();
    for (int i = 0; i < nElements; i++)
      values[i] = it.next().intValue();
    return new Int32I(values, dims);
  }

  public DataI toInt64I() {
    long[] values = new long[nElements];
    Iterator<BigDecimal> it = this.values.iterator();
    for (int i = 0; i < nElements; i++)
      values[i] = it.next().longValue();
    return new Int64I(values, dims);
  }

  public class DecimalIRawWrap {
    private int scale;
    private int length;
    private byte[] bigInteger;

    public DecimalIRawWrap(BigDecimal bd) {
      this.scale = bd.scale();
      this.bigInteger = bd.unscaledValue().toByteArray();
      this.length = this.bigInteger.length;
    }

    public int getScale() {
      return scale;
    }

    public int getLength() {
      return length;
    }

    public byte[] getBigInteger() {
      return bigInteger;
    }
  }

  public DataI toRawI() {
    List<DecimalIRawWrap> wraps = new LinkedList<>();
    for (BigDecimal bd : this.values)
      wraps.add(new DecimalIRawWrap(bd));
    List<byte[]> values = new LinkedList<>();
    for (DecimalIRawWrap w : wraps) {
      byte[] value = new byte[w.getLength() + 4 + 4];
      System.arraycopy(ByteBuffer.allocate(4).putInt(w.getScale()).array(), 0,
          value, 0, 3);
      System.arraycopy(ByteBuffer.allocate(4).putInt(w.getLength()).array(), 0,
          value, 4, 7);
      System.arraycopy(w.getBigInteger(), 0, value, 8, 8 + w.getLength() - 1);
      values.add(value);
    }
    return new RawI(values, dims);
  }

  public DataI toTextI() {
    String[] values = new String[nElements];
    int i = 0;
    for (BigDecimal bd : this.values)
      values[i++] = bd.toString();
    return new TextI(values, dims);
  }
}
