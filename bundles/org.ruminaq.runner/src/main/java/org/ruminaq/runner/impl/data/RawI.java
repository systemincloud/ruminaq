package org.ruminaq.runner.impl.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.runner.thrift.RemoteData;

public class RawI extends DataI {

    private static final long serialVersionUID = 1L;

    private List<byte[]> values = new LinkedList<>();

    private RawI() { super(new LinkedList<Integer>()); }

    public RawI(byte[] value) {
        this(value, false);
    }

    public RawI(byte[] value, boolean copy) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        if(copy) {
            byte[] values = new byte[value.length];
            System.arraycopy(values, 0, this.values, 0, values.length);
            this.values.add(values);
        } else this.values.add(value);
    }

    public RawI(List<byte[]> values, List<Integer> dims) {
        this(values, dims, false);
    }

    public RawI(List<byte[]> values, List<Integer> dims, boolean copy) {
        super(dims);
        if(copy) {
            for(byte[] v : values) this.values.add(Arrays.copyOf(v, v.length));
        } else this.values.addAll(values);
    }

    public RawI(byte[] values, List<Integer> dims) {
        this(values, dims, false);
    }

    public RawI(byte[] values, List<Integer> dims, boolean copy) {
        super(dims);
        if(copy) {
            byte[] _values = new byte[values.length];
            System.arraycopy(values, 0, _values, 0, values.length);
            this.values.add(_values);
        } this.values.add(values);
    }

    public RawI(RemoteData data) {
        super(data.getDims());
        byte[] bs = data.getBuf();
        int k = 0;
        while(k < bs.length) {
            int l = ByteBuffer.wrap(Arrays.copyOfRange(bs, k, k + 4)).getInt();
            this.values.add(Arrays.copyOfRange(bs, k + 4, k + 4 + l));
            k+=(k + 4 + l);
        }
    }

    @Override
    public RemoteData getRemoteData() {
        RemoteData rd = new RemoteData();
        rd.dims = this.dims;
        rd.type = "Raw";

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        for(byte[] v : this.values) {
            try {
                byteStream.write(ByteBuffer.allocate(4).putInt(v.length).array());
                byteStream.write(v);
            } catch (IOException e) { }
        }
        rd.buf = ByteBuffer.wrap(byteStream.toByteArray());

        return rd;
    }

    public List<byte[]> getValues() { return values; }

    @Override public String toString()      { return toTextI().toString(); }
    @Override public String toShortString() { return toTextI().toShortString(); }

    @Override protected DataI to(Class<? extends DataI> clazz) {
             if(BoolI     .class.isAssignableFrom(clazz)) return toBoolI();
        else if(Complex32I.class.isAssignableFrom(clazz)) return toComplex32I();
        else if(Complex64I.class.isAssignableFrom(clazz)) return toComplex64I();
        else if(ControlI  .class.isAssignableFrom(clazz)) return toControlI();
        else if(DecimalI  .class.isAssignableFrom(clazz)) return toDecimalI();
        else if(Float32I  .class.isAssignableFrom(clazz)) return toFloat32I();
        else if(Float64I  .class.isAssignableFrom(clazz)) return toFloat64I();
        else if(Int32I    .class.isAssignableFrom(clazz)) return toInt32I();
        else if(Int64I    .class.isAssignableFrom(clazz)) return toInt64I();
        else if(RawI      .class.isAssignableFrom(clazz)) return this;
        else if(TextI     .class.isAssignableFrom(clazz)) return toTextI();
        return from(clazz);
    }

    //
    // Converters
    //

    public DataI toBoolI() {
        boolean[] values = new boolean[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.values.get(0)[i] != (byte) 0;
        return new BoolI(values, dims);
    }

    public DataI toComplex32I() {
        float[] realValues = new float[nElements];
        float[] imagValues = new float[nElements];
        FloatBuffer buf = ByteBuffer.wrap(this.values.get(0))
                                    .asFloatBuffer();
        float[] array = new float[buf.remaining()];
        buf.get(array);
        for(int i = 0; i < nElements; i++)
            realValues[i] = array[i];
        for(int i = nElements; i < nElements << 1; i++)
            imagValues[i] = array[i];
        return new Complex32I(realValues, imagValues, dims);
    }

    public DataI toComplex64I() {
        double[] realValues = new double[nElements];
        double[] imagValues = new double[nElements];
        DoubleBuffer buf = ByteBuffer.wrap(this.values.get(0))
                                     .asDoubleBuffer();
        double[] array = new double[buf.remaining()];
        buf.get(array);
        for(int i = 0; i < nElements; i++)
            realValues[i] = array[i];
        for(int i = nElements; i < nElements << 1; i++)
            imagValues[i] = array[i];
        return new Complex64I(realValues, imagValues, dims);
    }

    public DataI toControlI() { return new ControlI(); }

    public DataI toDecimalI() {
        List<BigDecimal> values = new LinkedList<>();
        for(byte[] v : this.values) {
            byte[] scaleBytes = Arrays.copyOfRange(v, 0, 3);
            int scale = ByteBuffer.wrap(scaleBytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer().array()[0];
            byte[] lengthBytes = Arrays.copyOfRange(v, 4, 7);
            int length = ByteBuffer.wrap(lengthBytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer().array()[0];
            byte[] biBytes = Arrays.copyOfRange(v, 8, 8 + length - 1);
            BigDecimal bd = new BigDecimal(new BigInteger(biBytes));
            bd.setScale(scale);
            values.add(bd);
        }
        return new DecimalI(values, dims);
    }

    public DataI toFloat32I() {
        FloatBuffer buf = ByteBuffer.wrap(this.values.get(0)).asFloatBuffer();
        float[] values = new float[buf.remaining()];
        buf.get(values);
        return new Float32I(values, dims);
    }

    public DataI toFloat64I() {
        DoubleBuffer buf = ByteBuffer.wrap(this.values.get(0)).asDoubleBuffer();
        double[] values = new double[buf.remaining()];
        buf.get(values);
        return new Float64I(values, dims);
    }

    public DataI toInt32I() {
        IntBuffer buf = ByteBuffer.wrap(this.values.get(0)).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        int[] values = new int[buf.remaining()];
        buf.get(values);
        return new Int32I(values, dims);
    }

    public DataI toInt64I() {
        LongBuffer buf = ByteBuffer.wrap(this.values.get(0)).order(ByteOrder.BIG_ENDIAN).asLongBuffer();
        long[] values = new long[buf.remaining()];
        buf.get(values);
        return new Int64I(values, dims);
    }

    public DataI toTextI() {
        List<String> values = new LinkedList<>();
        for(byte[] v : this.values) {
            byte[] lengthBytes = Arrays.copyOfRange(v, 0, 3);
            IntBuffer intBuffer = ByteBuffer.wrap(lengthBytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
            if(intBuffer.capacity() == 0) values.add(new String(v));
            else {
                int length = ByteBuffer.wrap(lengthBytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer().array()[0];
                byte[] sBytes = Arrays.copyOfRange(v, 4, 4 + length - 1);
                String s = new String(sBytes);
                values.add(s);
            }
        }
        return new TextI(values, dims);
    }
}
