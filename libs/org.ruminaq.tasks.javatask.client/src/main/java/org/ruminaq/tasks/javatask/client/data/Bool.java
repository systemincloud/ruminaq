package org.ruminaq.tasks.javatask.client.data;

import java.util.LinkedList;
import java.util.List;

public class Bool extends Data {

    private boolean[] values;

    public Bool(boolean value) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        values = new boolean[] { value };
    }

    public Bool(List<Integer> dims, boolean[] values) {
        this(dims, values, false);
    }

    public Bool(List<Integer> dims, boolean[] values, boolean copy) {
        super(dims);
        if(copy) {
            this.values = new boolean[values.length];
            System.arraycopy(values, 0, this.values, 0, values.length);
        } else this.values = values;
    }

    public boolean[] getValues() { return values; }
    public boolean   getValue()  { return values[0]; }
}
