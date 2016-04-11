package com.weh.idgen.exception;

import com.weh.idgen.model.GenerateUniqueID;


/**
 * 
 * IDGeneratorException Class<br>
 * Exception handled when failed to connect to read or write file.
 * 
 * @author BizRuntime
 */
public class IDGeneratorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5102687805262282377L;

	public IDGeneratorException() {
		super();
	}

	public IDGeneratorException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IDGeneratorException(String message, Throwable cause) {
		super(message, cause);
		new GenerateUniqueID(message +"  "+ cause );
	}

	public IDGeneratorException(String message) {
		super(message);
		new GenerateUniqueID(message+" ");
	}

	public IDGeneratorException(Throwable cause) {
		super(cause);
	}
	
	

}
