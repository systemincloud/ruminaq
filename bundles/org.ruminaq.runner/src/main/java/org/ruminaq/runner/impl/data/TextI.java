package org.ruminaq.runner.impl.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.runner.thrift.RemoteData;

public class TextI extends DataI {

    private static final long serialVersionUID = 1L;

    public enum Separator {
        TAB("\t"),
        SPACE(" ");

        private String chars;
        public  String getChars() { return chars; }

        Separator(String chars) {
            this.chars = chars;
        }
    }

    private List<String> values = new LinkedList<>();

    private TextI() { super(new LinkedList<Integer>()); }

    public TextI(String value) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        this.values.add(value);
    }

    public TextI(List<String> values, List<Integer> dims) {
        super(dims);
        this.values.addAll(values);
    }

    public TextI(String[] values, List<Integer> dims) {
        super(dims);
        this.values.addAll(Arrays.asList(values));
    }

    public TextI(RemoteData data) {
        super(data.getDims());
        byte[] bs = data.getBuf();
        int k = 0;
        while(k < bs.length) {
            int l = ByteBuffer.wrap(Arrays.copyOfRange(bs, k, k + 4)).getInt();
            this.values.add(new String(Arrays.copyOfRange(bs, k + 4, k + 4 + l)));
            k = k + 4 + l;
        }
    }

    @Override
    public RemoteData getRemoteData() {
        RemoteData rd = new RemoteData();
        rd.dims = this.dims;
        rd.type = "Text";

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        for(String v : this.values) {
            try {
                byteStream.write(ByteBuffer.allocate(4).putInt(v.getBytes().length).array());
                byteStream.write(v.getBytes());
            } catch (IOException e) { }
        }
        rd.buf = ByteBuffer.wrap(byteStream.toByteArray());

        return rd;
    }

    public List<String> getValues() { return values; }

    public String toString(boolean pretty, int limit) {
        return toString(pretty, limit, Separator.TAB);
    }

    public String toString(boolean isPretty, int limit, Separator separator) {
        StringBuilder value = new StringBuilder("");
        if(this.getDimensions().size() == 1 && this.getDimensions().get(0) == 1) value.append(this.getValues().get(0));
        else if(this.getDimensions().size() == 1 && this.getDimensions().get(0) != 1) {
            value.append("[");
            for(String s : this.getValues()) {
                value.append(s).append(" ");
                if(limit > 0 && value.length() > limit) return value.append(" ...").toString();
            }
            value.deleteCharAt(value.length() - 1);
            value.append("]");
        } else if(this.getDimensions().size() == 2) {
            value.append("[");
            if(isPretty) value.append(separator.getChars());
            int n = this.getDimensions().get(1);
            int i = 0;
            for(String s : this.getValues()) {
                value.append(s);
                i++;
                if(isPretty) value.append(separator.getChars() + separator.getChars());
                else         value.append(", ");
                if(i%n == 0) {
                    if(isPretty) value.append("\n " + separator.getChars());
                    else         value.delete(value.length() - 2, value.length() - 1).append("; ");

                }
                if(limit > 0 && value.length() > limit) return value.append(" ...").toString();
            }
            value.delete(value.length() - 3, value.length());
            value.append("]");
        } else {
            value.append("++++\n");

            List<Integer> ids = new LinkedList<>();
            for(int i = 0; i < this.getDimensions().size() - 2; i++) ids.add(0);

            int i = 0;
            int n = this.getDimensions().get(1);
            int mn = this.getDimensions().get(0) * this.getDimensions().get(1);
            for(String s : this.getValues()) {
                if(i%mn == 0) {
                    value.append("[:,:");
                    for(Integer idx : ids) value.append(",").append(idx);
                    value.append("]:\n[");
                }

                value.append(s);
                i++;

                if(isPretty) value.append(separator.getChars() + separator.getChars());
                else         value.append(" ");
                if(i%n == 0) {
                    if(isPretty) value.append("\n " + separator.getChars());
                    else         value.append("; ");
                }
                if(i%mn == 0) {
                    value.delete(value.length() - 3, value.length());
                    value.append("]\n");
                    for(int x = 0; x < ids.size(); x++) {
                        if(ids.get(x) + 1 == this.getDimensions().get(x + 2)) ids.set(x, 0);
                        else                                                { ids.set(x, ids.get(x) + 1); break; }
                    }
                }
                if(limit > 0 && value.length() > limit) return value.append(" ...").toString();
            }
            value.append("----");
        }
        return value.toString();
    }

    public String toString()      { return toString(false, -1); }
    public String toShortString() { return toString(false, 50); }

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
        else if(RawI      .class.isAssignableFrom(clazz)) return toRawI();
        else if(TextI     .class.isAssignableFrom(clazz)) return this;
        return from(clazz);
    }

    //
    // Converters
    //

    public DataI toBoolI() {
        boolean[] values = new boolean[nElements];
        Iterator<String> it = this.values.iterator();
        for(int i = 0; i < nElements; i++)
            values[i] = Boolean.parseBoolean(it.next());
        return new BoolI(values, dims);
    }

    public DataI toComplex32I() {
        float[] real = new float[nElements];
        float[] imag = new float[nElements];
        Iterator<String> it = this.values.iterator();
        for(int i = 0; i < nElements; i++) {
            String s = it.next();
            if     (s.matches("[-]?[0-9]*\\.?[0-9]+"))  real[i] = Float.parseFloat(s);
            else if(s.matches("[-]?i[0-9]*\\.?[0-9]+")) imag[i] = Float.parseFloat(s);
            else {
                int k = s.contains("+") ? s.indexOf("+") : s.indexOf("-");
                real[i] = Float.parseFloat(s.substring(0, k).trim());
                imag[i] = Float.parseFloat(s.substring(s.indexOf("i"), s.length()).trim());
            }
        }
        return new Complex32I(real, imag, dims);
    }

    public DataI toComplex64I() {
        double[] real = new double[nElements];
        double[] imag = new double[nElements];
        Iterator<String> it = this.values.iterator();
        for(int i = 0; i < nElements; i++) {
            String s = it.next();
            if     (s.matches("[-]?[0-9]*\\.?[0-9]+"))  real[i] = Double.parseDouble(s);
            else if(s.matches("[-]?i[0-9]*\\.?[0-9]+")) imag[i] = Double.parseDouble(s);
            else {
                int k = s.contains("+") ? s.indexOf("+") : s.indexOf("-");
                real[i] = Double.parseDouble(s.substring(0, k).trim());
                imag[i] = Double.parseDouble(s.substring(s.indexOf("i"), s.length()).trim());
            }
        }
        return new Complex64I(real, imag, dims);
    }

    public DataI toControlI() { return new ControlI(); }

    public DataI toDecimalI() {
        BigDecimal[] values = new BigDecimal[nElements];
        Iterator<String> it = this.values.iterator();
        for(int i = 0; i < nElements; i++)
            values[i] = new BigDecimal(it.next());
        return new DecimalI(values, dims);
    }

    public DataI toFloat32I() {
        float[] values = new float[nElements];
        Iterator<String> it = this.values.iterator();
        for(int i = 0; i < nElements; i++)
            values[i] = Float.parseFloat(it.next());
        return new Float32I(values, dims);
    }

    public DataI toFloat64I() {
        double[] values = new double[nElements];
        Iterator<String> it = this.values.iterator();
        for(int i = 0; i < nElements; i++)
            values[i] = Double.parseDouble(it.next());
        return new Float64I(values, dims);
    }

    public DataI toInt32I() {
        int[] values = new int[nElements];
        Iterator<String> it = this.values.iterator();
        for(int i = 0; i < nElements; i++)
            values[i] = Integer.parseInt(it.next());
        return new Int32I(values, dims);
    }

    public DataI toInt64I() {
        long[] values = new long[nElements];
        Iterator<String> it = this.values.iterator();
        for(int i = 0; i < nElements; i++)
            values[i] = Long.parseLong(it.next());
        return new Int64I(values, dims);
    }

    public class TextIRawWrap {
        private int    length;
        private byte[] string;

        public TextIRawWrap(String s) {
            this.string = s.getBytes();
            this.length = this.string.length;
        }

        public int    getLength() { return length; }
        public byte[] getString() { return string; }
    }

    public DataI toRawI() {
        List<TextIRawWrap> wraps = new LinkedList<>();
        for(String s : this.values)
            wraps.add(new TextIRawWrap(s));
        List<byte[]> values = new LinkedList<>();
        for(TextIRawWrap w : wraps) {
            byte[] value = new byte[w.getLength() + 4];
            int ptr = 0;
            System.arraycopy(ByteBuffer.allocate(4).putInt(w.getLength()).array(), 0, value, ptr , 4);
            ptr += 4;
            System.arraycopy(w.getString(),                                        0, value, ptr , w.getLength());
            values.add(value);
        }
        return new RawI(values, dims);
    }
}
