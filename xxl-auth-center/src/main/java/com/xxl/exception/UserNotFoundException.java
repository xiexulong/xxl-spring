package com.xxl.exception;

public class UserNotFoundException extends HttpResourceNotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}