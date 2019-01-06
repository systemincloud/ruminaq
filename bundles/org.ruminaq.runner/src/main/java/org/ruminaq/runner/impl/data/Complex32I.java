package org.ruminaq.runner.impl.data;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.runner.thrift.RemoteData;

public class Complex32I extends NumericI {

    private static final long serialVersionUID = 1L;

    private float[] real;
    private float[] imag;

    private Complex32I() { super(new LinkedList<Integer>()); }

    public Complex32I(float real, float imag) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        this.real = new float[] { real };
        this.imag = new float[] { imag };
    }

    public Complex32I(float[] real, float[] imag, List<Integer> dims) {
        this(real, imag, dims, false);
    }

    public Complex32I(float[] real, float[] imag, List<Integer> dims, boolean copy) {
        super(dims);
        if(copy) {
            this.real = new float[real.length];
            this.imag = new float[imag.length];
            System.arraycopy(real, 0, this.real, 0, real.length);
            System.arraycopy(imag, 0, this.imag, 0, imag.length);
        } else {
            this.real = real;
            this.imag = imag;
        }
    }

    public Complex32I(RemoteData data) {
        super(data.getDims());
        try {
            FloatBuffer fb = ByteBuffer.wrap(data.getBuf()).asFloatBuffer();
            float[] array = new float[fb.limit()];
            fb.get(array);

            this.real = new float[array.length >> 1];
            this.imag = new float[array.length >> 1];

            for(int i = 0; i < array.length >> 1; i++)
                this.real[i] = array[i];

            for(int i = 0; i < array.length >> 1; i++)
                this.imag[i] = array[i + (array.length >> 1)];
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RemoteData getRemoteData() {
        RemoteData rd = new RemoteData();
        rd.dims = this.dims;
        rd.type = "Complex32";
        float[] array = new float[this.real.length + this.imag.length];

        for(int i = 0; i < real.length; i++)
            array[i] = this.real[i];

        for(int i = 0; i < imag.length; i++)
            array[i + imag.length] = this.imag[i];

        ByteBuffer byteBuffer = ByteBuffer.allocate(array.length << 2);
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(array);
        rd.buf = byteBuffer;
        return rd;
    }

    public float[] getRealValues() { return real; }
    public float[] getImagValues() { return imag; }

    @Override public String toString()      { return toTextI().toString(); }
    @Override public String toShortString() { return toTextI().toShortString(); }

    @Override protected DataI to(Class<? extends DataI> clazz) {
             if(BoolI     .class.isAssignableFrom(clazz)) return toBoolI();
        else if(Complex32I.class.isAssignableFrom(clazz)) return this;
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

    public DataI toBoolI() {
        boolean[] values = new boolean[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.real[i] != 0 || this.imag[i] != 0;
        return new BoolI(values, dims);
    }

    public DataI toComplex64I() {
        double[] realValues = new double[nElements];
        double[] imagValues = new double[nElements];
        for(int i = 0; i < nElements; i++)
            realValues[i] = this.real[i];
        for(int i = 0; i < nElements; i++)
            imagValues[i] = this.imag[i];
        return new Complex64I(realValues, imagValues, dims);
    }

    public DataI toControlI() { return new ControlI(); }

    public DataI toDecimalI() {
        BigDecimal[] values = new BigDecimal[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = new BigDecimal(this.real[i]);
        return new DecimalI(values, dims);
    }

    public DataI toFloat32I() {
        float[] values = new float[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.real[i];
        return new Float32I(values, dims);
    }

    public DataI toFloat64I() {
        double[] values = new double[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = this.real[i];
        return new Float64I(values, dims);
    }

    public DataI toInt32I() {
        int[] values = new int[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = (int) this.real[i];
        return new Int32I(values, dims);
    }

    public DataI toInt64I() {
        long[] values = new long[nElements];
        for(int i = 0; i < nElements; i++)
            values[i] = (long) this.real[i];
        return new Int64I(values, dims);
    }

    public DataI toRawI() {
        byte[] values = new byte[2 * nElements * 4];
        byte[] realBytes = new byte[nElements * 4];
        byte[] imagBytes = new byte[nElements * 4];
        ByteBuffer.wrap(realBytes).asFloatBuffer().put(this.real);
        ByteBuffer.wrap(imagBytes).asFloatBuffer().put(this.imag);
        System.arraycopy(realBytes, 0, values, 0,                realBytes.length);
        System.arraycopy(imagBytes, 0, values, imagBytes.length, imagBytes.length);
        return new RawI(values, dims);
    }

    public DataI toTextI() {
        String[] values = new String[nElements];
        for(int i = 0; i < nElements; i++) {
            String sign = this.imag[i] < 0 ? " - " : " + ";
            values[i] = Float.toString(this.real[i]) + sign + Float.toString(Math.abs(this.imag[i])) + "i";
        }
        return new TextI(values, dims);
    }
}
