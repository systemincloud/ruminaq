package org.ruminaq.runner.impl.debug.events.debugger;

import java.io.Serializable;

public class NewInputPort implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  public String getId() {
    return id;
  }

  public NewInputPort(String id) {
    this.id = id;
  }
}
