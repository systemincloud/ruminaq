package org.ruminaq.runner.impl.debug.events.model;

import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IModelRequest;

public class PortBreakpointRequest extends AbstractPortEvent implements IModelRequest {

	private static final long serialVersionUID = 1L;

	public enum Type { ADDED, REMOVED }

	private Type    type;
	private int     hitCount;
	private boolean suspendAll;

	public PortBreakpointRequest(Type type, AbstractPortEventListener apel) {
		super(apel);
		this.type = type;
	}

	public PortBreakpointRequest(Type type, int hitCount, boolean suspendAll, AbstractPortEventListener apel) {
		super(apel);
		this.type       = type;
		this.hitCount   = hitCount;
		this.suspendAll = suspendAll;
	}

	public Type    getType()     { return type; }
	public int     getHitCount() { return hitCount; }
	public boolean isSupendAll() { return suspendAll; }
}
