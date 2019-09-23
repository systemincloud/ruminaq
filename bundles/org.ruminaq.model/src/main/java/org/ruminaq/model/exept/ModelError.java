package org.ruminaq.model.exept;

public class ModelError extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ModelError(String msg) {
    super(msg);
  }
}
