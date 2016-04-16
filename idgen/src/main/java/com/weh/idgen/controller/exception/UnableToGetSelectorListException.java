package com.weh.idgen.controller.exception;

import com.weh.idgen.controller.IDGenControllerException;

/**
 * UnableToGetSelectorListException Class<br>
 * Exception handled when bad request of selector or selector not found.
 * @author BizRuntime
 */
public class UnableToGetSelectorListException extends IDGenControllerException {

	private static final long serialVersionUID = 5102687805262282377L;

	public UnableToGetSelectorListException() {
		super();
	}

	public UnableToGetSelectorListException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnableToGetSelectorListException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnableToGetSelectorListException(String message) {
		super(message);
	}

	public UnableToGetSelectorListException(Throwable cause) {
		super(cause);
	}

}
