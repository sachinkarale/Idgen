package com.weh.idgen.helper;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * IDGenExceptionHelper class sends the error message by taking the error code.
 * 
 * @author BizRuntime
 */
public class IDGenExceptionHelper {

	protected static final Logger logger = Logger.getLogger(IDGenExceptionHelper.class);

	private static String errorCode;
	private static String errorMessage;

	public static String getErrorCode() {
		return errorCode;
	}

	public static void setErrorCode(String errorCode) {
		IDGenExceptionHelper.errorCode = errorCode;
	}

	public static String getErrorMessage() {
		return errorMessage;
	}

	public static void setErrorMessage(String errorMessage) {
		IDGenExceptionHelper.errorMessage = errorMessage;
	}

	/**
	 * This method reads the errorCode propeties file, 
	 * and returns error message with errorCode.
	 * @param errorCode : errorCode from catch block
	 * @return error message for particular error code
	 */
	public static String exceptionFormat(String errorCode) {
		IDGenExceptionHelper.errorCode = errorCode;
		Properties prop = null;
		try {
			prop = IDGenConfigHelper.getErrorCodeProperties();
		} catch (IDGenInitializationException e) {
		}
		String message = (String) prop.get(errorCode);
		errorMessage = "ErrorCode=" + errorCode + ", message=" + message;
		return errorMessage;
	}

}
