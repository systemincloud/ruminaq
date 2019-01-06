package org.ruminaq.runner.impl.debug;

public interface DebugListener {
	void steppingSuspend();
	void breakpointSuspend();
	void suspendAll();
}
