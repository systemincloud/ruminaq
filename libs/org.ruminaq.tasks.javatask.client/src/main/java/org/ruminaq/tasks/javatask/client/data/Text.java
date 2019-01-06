package org.ruminaq.tasks.javatask.client.data;

import java.util.LinkedList;
import java.util.List;

public class Text extends Data {

    private List<String> values = new LinkedList<>();

    public Text(List<Integer> dims, List<String> values) {
        super(dims);
        this.values.addAll(values);
    }

    public Text(String value) {
        super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
        this.values.add(value);
    }

    public List<String> getValues() { return values; }
    public String       getValue()  { return values.get(0); }

}
