package org.ruminaq.tasks.javatask.client.data;

import java.util.LinkedList;
import java.util.List;

public class Float64 extends Data {

    private double[] values;

    public Float64(double value) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        values = new double[] { value };
    }

    public Float64(List<Integer> dims, double[] values) {
        this(dims, values, false);
    }

    public Float64(List<Integer> dims, double[] values, boolean copy) {
        super(dims);
        if(copy) {
            this.values = new double[values.length];
            System.arraycopy( values, 0, this.values, 0, values.length );
        } else this.values = values;
    }

    public double[] getValues() { return values; }
    public double   getValue()  { return values[0]; }
}
