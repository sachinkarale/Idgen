package com.weh.idgen.controller.exception;

import com.weh.idgen.controller.IDGenControllerException;

/**
 * 
 * IDGeneratorException Class<br>
 * Exception handled when failed to connect to write file.
 * 
 * @author BizRuntime
 */
public class UnableToWriteFileException extends IDGenControllerException {

	private static final long serialVersionUID = 5102687805262282377L;

	public UnableToWriteFileException() {
		super();
	}

	public UnableToWriteFileException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnableToWriteFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnableToWriteFileException(String message) {
		super(message);
	}

	public UnableToWriteFileException(Throwable cause) {
		super(cause);
	}

}
