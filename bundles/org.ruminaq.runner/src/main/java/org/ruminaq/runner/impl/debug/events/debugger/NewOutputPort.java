package org.ruminaq.runner.impl.debug.events.debugger;

import java.io.Serializable;

public class NewOutputPort implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	
	public String getId()   { return id; }
	
	public NewOutputPort(String id) {
		this.id   = id;
	}
}
