package org.ruminaq.runner.impl.debug.events.model;

import org.ruminaq.runner.impl.debug.events.AbstractEvent;
import org.ruminaq.runner.impl.debug.events.IModelRequest;

public class SuspendAllPortRequest extends AbstractEvent
    implements IModelRequest {

  private static final long serialVersionUID = 1L;

  public SuspendAllPortRequest() {
  }
}
