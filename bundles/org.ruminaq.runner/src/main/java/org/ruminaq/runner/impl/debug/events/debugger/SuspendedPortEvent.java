package org.ruminaq.runner.impl.debug.events.debugger;

import org.eclipse.debug.core.DebugEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IDebuggerEvent;

public class SuspendedPortEvent extends AbstractPortEvent implements IDebuggerEvent {

	private static final long serialVersionUID = 1L;

	public enum Type { CLIENT    (DebugEvent.CLIENT_REQUEST),
		               BREAKPOINT(DebugEvent.BREAKPOINT),
		               STEP_OVER (DebugEvent.STEP_OVER),
		               INIT      (DebugEvent.UNSPECIFIED);
		private int debugType;
		public  int getDebugType() { return debugType; }
		Type(int debugType) {
			this.debugType = debugType;
		}
	}

	private final Type type;

	public Type getType() { return type; }

	public SuspendedPortEvent(Type type, AbstractPortEventListener apel) {
		super(apel);
		this.type = type;
	}
}
