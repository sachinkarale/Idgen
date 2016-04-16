package com.weh.idgen.controller.exception;

import com.weh.idgen.controller.IDGenControllerException;

/**
 * UnableToReadFileException Class<br>
 * Exception handled when failed to connect to read file.
 * @author BizRuntime
 */
public class UnableToReadFileException extends IDGenControllerException {

	private static final long serialVersionUID = 5102687805262282377L;

	public UnableToReadFileException() {
		super();
	}

	public UnableToReadFileException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnableToReadFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnableToReadFileException(String message) {
		super(message);
	}

	public UnableToReadFileException(Throwable cause) {
		super(cause);
	}

}
