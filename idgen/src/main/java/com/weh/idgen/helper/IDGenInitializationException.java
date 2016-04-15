package com.weh.idgen.helper;

import com.weh.idgen.controller.IDGenControllerException;

/**
 * <h3>IDGeneratorInitializationException Class</h3> Exception handled when
 * failed to initialize File path from the properties.
 * 
 * @author BizRuntime
 */
public class IDGenInitializationException extends IDGenControllerException {

	private static final long serialVersionUID = -7877294998139008453L;

	public IDGenInitializationException() {
		super();
	}

	public IDGenInitializationException(String message) {
		super(message);
	}

	public IDGenInitializationException(Throwable cause) {
		super(cause);
	}

	public IDGenInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public IDGenInitializationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
