package com.sample.poc.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class KeyExistException extends RuntimeException {
    public KeyExistException (String exception) {
        super (exception);
    }
}
