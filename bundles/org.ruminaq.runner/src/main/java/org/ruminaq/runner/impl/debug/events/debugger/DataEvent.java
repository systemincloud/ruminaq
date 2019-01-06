package org.ruminaq.runner.impl.debug.events.debugger;

import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IDebuggerEvent;

public class DataEvent extends AbstractPortEvent implements IDebuggerEvent {

	private static final long serialVersionUID = 1L;

	private DataI data;

	public DataI getData() { return data; }

	public DataEvent(DataI data, AbstractPortEventListener apel) {
		super(apel);
		this.data = data;
	}

	@Override public void preevaluate() { }
}
