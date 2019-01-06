package org.ruminaq.runner.impl.debug.events.model;

import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IModelRequest;

public class FetchDataQueueRequest extends AbstractPortEvent implements IModelRequest {

	private static final long serialVersionUID = 1L;

	public FetchDataQueueRequest(AbstractPortEventListener apel) {
		super(apel);
	}
}
