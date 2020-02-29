package com.alex.factory.controller;

import com.alex.factory.exception.CompFactOrderNotFoundException;
import com.alex.factory.exception.CompFactProductNotFoundException;
import com.alex.factory.exception.CompFactSuchUserAlreadyExistException;
import com.alex.factory.exception.CompFactWrongPasswordException;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;
import java.util.logging.Level;

@ControllerAdvice
@Log
public class ExceptionControllerAdvice {

    @ExceptionHandler(
            {CompFactOrderNotFoundException.class, CompFactProductNotFoundException.class, CompFactSuchUserAlreadyExistException.class,
                    UsernameNotFoundException.class, NoSuchElementException.class, CompFactWrongPasswordException.class})
    private ResponseEntity<ErrorMessage> handleBadRequest(final Exception e) {
        log.log(Level.SEVERE, e.getMessage(), e);
        return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handle(final Exception e) {
        log.log(Level.SEVERE, e.getMessage(), e);
        return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ErrorMessage> handleForbidden(final Exception e) {
        log.log(Level.SEVERE, e.getMessage(), e);
        return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @Data
    public static class ErrorMessage {
        private final String errorMessage;
    }
}
