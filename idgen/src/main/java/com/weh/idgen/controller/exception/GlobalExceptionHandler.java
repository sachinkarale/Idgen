package com.weh.idgen.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.weh.idgen.controller.IDGenControllerException;

/**
 * GlobalExceptionHandler class
 * initialize the IDGenControllerException and its sub class.
 * @author BizRuntime
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = IDGenControllerException.class)
	public String handleBaseException(IDGenControllerException e) {
		return e.getMessage();
	}

	@ExceptionHandler(value = Exception.class)
	public String handleException(Exception e) {
		return e.getMessage();
	}

}
