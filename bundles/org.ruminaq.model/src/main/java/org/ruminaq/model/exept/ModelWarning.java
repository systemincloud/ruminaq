package org.ruminaq.model.exept;

public class ModelWarning extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Object ret;
	
	public ModelWarning(String msg, Object ret) {
		super(msg);
		this.ret = ret;
	}
	
	public Object getReturn() {
		return ret;
	}

}
