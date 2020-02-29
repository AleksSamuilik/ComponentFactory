package com.alex.factory.exception;

public class CompFactWrongPasswordException extends Exception{
    public CompFactWrongPasswordException() {
    }

    public CompFactWrongPasswordException(String message) {
        super(message);
    }
}
