package org.ruminaq.tasks.javatask.client.data;

import java.util.LinkedList;

public class Control extends Data {

	public Control() {
		super(new LinkedList<Integer>() { private static final long serialVersionUID = 1L; { add(1); }});
	}
}
