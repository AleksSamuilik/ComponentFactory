package com.alex.factory.exception;

public class SuchUserAlreadyExistException extends Exception {

    public SuchUserAlreadyExistException() {
        super();
    }

    public SuchUserAlreadyExistException(final String message) {
        super(message);
    }
}
