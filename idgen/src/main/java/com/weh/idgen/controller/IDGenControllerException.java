package com.weh.idgen.controller;

/**
 * IDGeneratorException Class<br>
 * Exception handled when failed to operator on controller class
 * @author BizRuntime
 */
public class IDGenControllerException extends Exception {

	private static final long serialVersionUID = 5102687805262282377L;

	public IDGenControllerException() {
		super();
	}

	public IDGenControllerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IDGenControllerException(String message, Throwable cause) {
		super(message, cause);
	}

	public IDGenControllerException(String message) {
		super(message);
	}

	public IDGenControllerException(Throwable cause) {
		super(cause);
	}

}
