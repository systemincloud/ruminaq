package org.ruminaq.tasks.javatask.client.data;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class Decimal extends Data {

    private List<BigDecimal> values = new LinkedList<>();

    public Decimal(BigDecimal value) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        this.values.add(value);
    }

    public Decimal(List<Integer> dims, List<BigDecimal> values) {
        super(dims);
        this.values.addAll(values);
    }

    public List<BigDecimal> getValues() { return values; }
    public BigDecimal       getValue()  { return values.get(0); }
}
