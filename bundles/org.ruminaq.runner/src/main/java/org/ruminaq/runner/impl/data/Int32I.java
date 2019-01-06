package org.ruminaq.runner.impl.data;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.runner.thrift.RemoteData;

public class Int32I extends NumericI {

    private static final long serialVersionUID = 1L;

    private int[] values;

    private Int32I() { super(new LinkedList<Integer>()); }

    public Int32I(int value) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        values = new int[] { value };
    }

    public Int32I(int[] values, List<Integer> dims) {
        this(values, dims, false);
    }

    public Int32I(int[] values, List<Integer> dims, boolean copy) {
        super(dims);
        if(copy) {
            this.values = new int[values.length];
            System.arraycopy(values, 0, this.values, 0, values.length);
        } else this.values = values;
    }

    public Int32I(RemoteData data) {
        super(data.getDims());
        try {
            IntBuffer ib = ByteBuffer.wrap(data.getBuf()).asIntBuffer();
            this.values = new int[ib.limit()];
            ib.get(this.values);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RemoteData getRemoteData() {
        RemoteData rd = new RemoteData();
        rd.dims = this.dims;
        rd.type = "Int32";

        ByteBuffer byteBuffer = ByteBuffer.allocate(this.values.length << 2);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(this.values);
        rd.buf = byteBuffer;
        return rd;
    }

    public int[] getValues() { return values; }

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
        else if(Int32I    .class.isAssignableFrom(clazz)) return this;
        else if(Int64I    .class.isAssignableFrom(clazz)) return toInt64I();
        else if(RawI      .class.isAssignableFrom(clazz)) return toRawI();
        else if(TextI     .class.isAssignableFrom(clazz)) return toTextI();
        return from(clazz);
    }

    //
    // Converters
    //

    public DataI toBoolI() {
        boolean[] values = new boolean[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.values[i] != 0;
        return new BoolI(values, dims);
    }

    public DataI toComplex32I() {
        float[] realValues = new float[nElements];
        float[] imagValues = new float[nElements];
        for(int i = 0; i < nElements; i++)
            realValues[i] = this.values[i];
        return new Complex32I(realValues, imagValues, dims);
    }

    public DataI toComplex64I() {
        double[] realValues = new double[nElements];
        double[] imagValues = new double[nElements];
        for(int i = 0; i < nElements; i++)
            realValues[i] = this.values[i];
        return new Complex64I(realValues, imagValues, dims);
    }

    public DataI toControlI() { return new ControlI(); }

    public DataI toDecimalI() {
        BigDecimal[] values = new BigDecimal[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = new BigDecimal(this.values[i]);
        return new DecimalI(values, dims);
    }

    public DataI toFloat32I() {
        float[] values = new float[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = (float) this.values[i];
        return new Float32I(values, dims);
    }

    public DataI toFloat64I() {
        double[] values = new double[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = (double) this.values[i];
        return new Float64I(values, dims);
    }

    public DataI toInt64I() {
        long[] values = new long[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = (long) this.values[i];
        return new Int64I(values, dims);
    }

    public DataI toRawI() {
        byte[] values = new byte[nElements * 4];
        ByteBuffer.wrap(values).asIntBuffer().put(this.values);
        return new RawI(values, dims);
    }

    public DataI toTextI() {
        String[] sValues = new String[nElements];
        for(int i = 0; i < nElements; i++)
            sValues[i] = Integer.toString(this.values[i]);
        return new TextI(sValues, dims);
    }
}
