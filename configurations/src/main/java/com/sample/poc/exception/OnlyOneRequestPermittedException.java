package com.sample.poc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class OnlyOneRequestPermittedException extends RuntimeException {

	public OnlyOneRequestPermittedException(String msg) {
		super(msg);
	}

}
