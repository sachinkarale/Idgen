package com.weh.idgen.controller.exception;

import com.weh.idgen.controller.IDGenControllerException;

/**
 * UnableToGetSelectorIDException Class<br>
 * Exception handled when failed to get Id from file.
 * @author BizRuntime
 */
public class UnableToGetSelectorIDException extends IDGenControllerException {

	private static final long serialVersionUID = 5102687805262282377L;

	public UnableToGetSelectorIDException() {
		super();
	}

	public UnableToGetSelectorIDException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnableToGetSelectorIDException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnableToGetSelectorIDException(String message) {
		super(message);
	}

	public UnableToGetSelectorIDException(Throwable cause) {
		super(cause);
	}

}
