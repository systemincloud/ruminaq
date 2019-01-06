package org.ruminaq.tasks.javatask.client.data;

import java.util.LinkedList;
import java.util.List;

public class Int64 extends Data {

    private long[] values;

    public Int64(long value) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        values = new long[] { value };
    }

    public Int64(List<Integer> dims, long[] values) {
        this(dims, values,false);
    }

    public Int64(List<Integer> dims, long[] values, boolean copy) {
        super(dims);
        if(copy) {
            this.values = new long[values.length];
            System.arraycopy( values, 0, this.values, 0, values.length );
        } else this.values = values;
    }

    public long[] getValues() { return values; }
    public long   getValue()  { return values[0]; }
}
