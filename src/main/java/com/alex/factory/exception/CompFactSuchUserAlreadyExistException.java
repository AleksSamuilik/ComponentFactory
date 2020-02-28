package com.alex.factory.exception;

public class CompFactSuchUserAlreadyExistException extends Exception {

    public CompFactSuchUserAlreadyExistException(){   }

    public CompFactSuchUserAlreadyExistException(final String message) {
        super(message);
    }
}
