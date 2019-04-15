package com.sample.poc.exception;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new RestException(HttpStatus.BAD_REQUEST, error, ex));
    }


    @ExceptionHandler(ConfigNotFoundException.class)
    protected ResponseEntity<Object> handleConfigNotFoundException(
            ConfigNotFoundException ex) {
        RestException authenticationException = new RestException(HttpStatus.NOT_FOUND);
        authenticationException.setMessage(ex.getMessage());
        return buildResponseEntity(authenticationException);
    }


    @ExceptionHandler(KeyExistException.class)
    protected  ResponseEntity<Object> handleKeyExistException(
            KeyExistException ex) {
        RestException authenticationException = new RestException(HttpStatus.NOT_ACCEPTABLE);
        authenticationException.setMessage(ex.getMessage());
        return buildResponseEntity(authenticationException);
    }


    @ExceptionHandler(ConfigurationException.class)
    protected  ResponseEntity<Object> handleConfigurationException(
        ConfigurationException ex) {
        RestException authenticationException = new RestException(HttpStatus.INTERNAL_SERVER_ERROR);
        authenticationException.setMessage(ex.getMessage());
        return buildResponseEntity(authenticationException);
    }

    @ExceptionHandler(OnlyOneRequestPermittedException.class)
    protected ResponseEntity<Object> handleOnlyOneRequestPermittedException(
            OnlyOneRequestPermittedException ex) {
        RestException authenticationException = new RestException(HttpStatus.TOO_MANY_REQUESTS);
        authenticationException.setMessage(ex.getMessage());
        return buildResponseEntity(authenticationException);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException ex) {
        RestException authenticationException = new RestException(HttpStatus.UNAUTHORIZED);
        authenticationException.setMessage(ex.getMessage());
        return buildResponseEntity(authenticationException);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            UsernameNotFoundException ex) {
        RestException authenticationException = new RestException(HttpStatus.NOT_FOUND);
        authenticationException.setMessage(ex.getMessage());
        return buildResponseEntity(authenticationException);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<Object> handlenknownUserException(
            UsernameNotFoundException ex) {
        RestException authenticationException = new RestException(HttpStatus.FORBIDDEN);
        authenticationException.setMessage(ex.getMessage());
        return buildResponseEntity(authenticationException);
    }

    private ResponseEntity<Object> buildResponseEntity(RestException exception) {
        return new ResponseEntity<>(exception, exception.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        RestException applicationException = new RestException(status);
        applicationException.setMessage(ex.getMessage());
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
        }

        return new ResponseEntity(applicationException, headers, status);
    }

}
