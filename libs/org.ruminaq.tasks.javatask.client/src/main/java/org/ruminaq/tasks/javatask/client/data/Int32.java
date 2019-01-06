package org.ruminaq.tasks.javatask.client.data;

import java.util.LinkedList;
import java.util.List;

public class Int32 extends Data {

    private int[] values;

    public Int32(int value) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        values = new int[] { value };
    }

    public Int32(List<Integer> dims, int[] values) {
        this(dims, values, false);
    }

    public Int32(List<Integer> dims, int[] values, boolean copy) {
        super(dims);
        if(copy) {
            this.values = new int[values.length];
            System.arraycopy( values, 0, this.values, 0, values.length );
        } else this.values = values;
    }

    public int[] getValues() { return values; }
    public int   getValue()  { return values[0]; }
}
