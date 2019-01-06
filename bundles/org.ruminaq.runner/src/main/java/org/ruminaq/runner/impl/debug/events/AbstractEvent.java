package org.ruminaq.runner.impl.debug.events;

public abstract class AbstractEvent implements IDebugEvent {

	private static final long serialVersionUID = 1L;

	@Override public String toString()    { return getClass().getSimpleName(); }
	@Override public void   preevaluate() { };
}
