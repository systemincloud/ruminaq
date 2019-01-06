package org.ruminaq.runner.impl.debug.events;

import java.io.Serializable;

public interface IDebugEvent extends Serializable {
	void preevaluate();
}
