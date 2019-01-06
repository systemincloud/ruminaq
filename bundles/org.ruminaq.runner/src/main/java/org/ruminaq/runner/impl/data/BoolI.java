package org.ruminaq.runner.impl.data;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.runner.thrift.RemoteData;

public class BoolI extends DataI {

    private static final long serialVersionUID = 1L;

    private boolean[] values;

    private BoolI() { super(new LinkedList<Integer>()); }

    public BoolI(boolean value) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        values = new boolean[] { value };
    }

    public BoolI(boolean[] values, List<Integer> dims) {
        this(values, dims, false);
    }

    public BoolI(boolean[] values, List<Integer> dims, boolean copy) {
        super(dims);
        if(copy) {
            this.values = new boolean[values.length];
            System.arraycopy(values, 0, this.values, 0, values.length);
        } else this.values = values;
    }

    public BoolI(RemoteData data) {
        super(data.getDims());
        boolean[] array = new boolean[data.getBuf().length];
        int k = 0;
        for(int i : data.getBuf())
            array[k++] = i != 0;
        this.values = array;
    }

    @Override
    public RemoteData getRemoteData() {
        RemoteData rd = new RemoteData();
        rd.dims = this.dims;
        rd.type = "Bool";
        int[] tmparray = new int[this.values.length];
        int k = 0;
        for(boolean b : this.values)
            tmparray[k++] = b ? 1 : 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(tmparray.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(tmparray);
        rd.buf = byteBuffer;
        return rd;
    }

    public boolean[] getValues() { return values; }

    @Override public String toString()      { return toTextI().toString(); }
    @Override public String toShortString() { return toTextI().toShortString(); }

    @Override protected DataI to(Class<? extends DataI> clazz) {
             if(BoolI     .class.isAssignableFrom(clazz)) return this;
        else if(Complex32I.class.isAssignableFrom(clazz)) return toComplex32I();
        else if(Complex64I.class.isAssignableFrom(clazz)) return toComplex64I();
        else if(ControlI  .class.isAssignableFrom(clazz)) return toControlI();
        else if(DecimalI  .class.isAssignableFrom(clazz)) return toDecimalI();
        else if(Float32I  .class.isAssignableFrom(clazz)) return toFloat32I();
        else if(Float64I  .class.isAssignableFrom(clazz)) return toFloat64I();
        else if(Int32I    .class.isAssignableFrom(clazz)) return toInt32I();
        else if(Int64I    .class.isAssignableFrom(clazz)) return toInt64I();
        else if(RawI      .class.isAssignableFrom(clazz)) return toRawI();
        else if(TextI     .class.isAssignableFrom(clazz)) return toTextI();
        return from(clazz);
    }

    //
    // Converters
    //

    public DataI toControlI() { return new ControlI(); }

    public DataI toComplex32I() {
        float[] realValues = new float[nElements];
        float[] imagValues = new float[nElements];
        for(int i = 0; i < nElements; i++)
            realValues[i] = this.values[i] ? 1.0f : 0.0f;
        return new Complex32I(realValues, imagValues, dims);
    }

    public DataI toComplex64I() {
        double[] realValues = new double[nElements];
        double[] imagValues = new double[nElements];
        for(int i = 0; i < nElements; i++)
            realValues[i] = this.values[i] ? 1.0d : 0.0d;
        return new Complex64I(realValues, imagValues, dims);
    }

    public DataI toDecimalI() {
        BigDecimal[] values = new BigDecimal[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.values[i] ? new BigDecimal(1) : new BigDecimal(0);
        return new DecimalI(values, dims);
    }

    public DataI toFloat32I() {
        float[] values = new float[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.values[i] ? 1.0f : 0.0f;
        return new Float32I(values, dims);
    }

    public DataI toFloat64I() {
        double[] values = new double[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.values[i] ? 1.0d : 0.0d;
        return new Float64I(values, dims);
    }

    public DataI toInt32I() {
        int[] values = new int[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.values[i] ? 1 : 0;
        return new Int32I(values, dims);
    }

    public DataI toInt64I() {
        long[] values = new long[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.values[i] ? 1L : 0L;
        return new Int64I(values, dims);
    }

    public DataI toRawI() {
        byte[] values = new byte[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.values[i] ? (byte) 1 : (byte) 0;
        return new RawI(values, dims);
    }

    public DataI toTextI() {
        String[] values = new String[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = Boolean.toString(this.values[i]);
        return new TextI(values, dims);
    }
}
