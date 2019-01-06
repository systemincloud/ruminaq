package org.ruminaq.tasks.javatask.client.data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Raw extends Data {

    private List<byte[]> values = new LinkedList<byte[]>();

    public Raw(byte[] value) {
        this(value, false);
    }

    public Raw(byte[] value, boolean copy) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        if(copy) {
            byte[] _values = Arrays.copyOf(value, value.length);
            this.values.add(_values);
        } else this.values.add(value);
    }

    public Raw(List<Integer> dims, byte[] values) {
        this(dims, values, false);
    }

    public Raw(List<Integer> dims, byte[] values, boolean copy) {
        super(dims);
        if(copy) {
            byte[] _values = new byte[values.length];
            System.arraycopy(values, 0, _values, 0, values.length);
            this.values.add(_values);
        } else this.values.add(values);
    }

    public Raw(List<Integer> dims, List<byte[]> values) {
        this(dims, values, false);
    }

    public Raw(List<Integer> dims, List<byte[]> values, boolean copy) {
        super(dims);
        if(copy) {
            for(byte[] v : values) this.values.add(Arrays.copyOf(v, v.length));
        } else this.values.addAll(values);
    }

    public List<byte[]> getValues() { return values; }
    public byte[]       getValue()  { return values.get(0); }
}
