package com.weh.idgen.controller;

import org.apache.log4j.Logger;

import com.weh.idgen.model.GenerateUniqueID;

/**
 * 
 * IDGeneratorInitializationException Class<br>
 * Exception handled when failed to initialize Filepath from the properties
 * 
 * @author BizRuntime
 */
public class IDGeneratorInitializationException extends Exception {
	
	private static final long serialVersionUID = -7877294998139008453L;
	// Logger
	protected static final Logger logger = Logger
			.getLogger(IDGeneratorInitializationException.class);

	public IDGeneratorInitializationException() {
		super();
	}

	public IDGeneratorInitializationException(String message) {
		super(message);
		new GenerateUniqueID(message+" ");
		
	}

	public IDGeneratorInitializationException(Throwable cause) {
		super(cause);

	}

	public IDGeneratorInitializationException(String message, Throwable cause) {
		super(message, cause);
		new GenerateUniqueID(message +"  "+ cause );

	}

	public IDGeneratorInitializationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
