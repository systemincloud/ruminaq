package org.ruminaq.runner.impl.debug.events.debugger;

import java.util.Map;

import org.ruminaq.runner.impl.debug.VariableDebugVisitor;
import org.ruminaq.runner.impl.debug.VariableDebugVisitor.Variable;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IDebuggerEvent;

public class InternalOutputPortVariablesEvent extends AbstractPortEvent implements IDebuggerEvent {

	private static final long serialVersionUID = 1L;

	private Map<Variable, Object> variables;

	public Map<Variable, Object> getVariables() { return variables; }

	public InternalOutputPortVariablesEvent(VariableDebugVisitor visitor, AbstractPortEventListener apel) {
		super(apel);
		this.variables = visitor.getVariables();
	}

	@Override public void preevaluate() { }
}
