package org.ruminaq.runner.impl.debug.events.model;

import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IModelRequest;

public class SuspendPortRequest extends AbstractPortEvent implements IModelRequest {

	private static final long serialVersionUID = 1L;

	public SuspendPortRequest(AbstractPortEventListener apel) {
		super(apel);
	}
}
