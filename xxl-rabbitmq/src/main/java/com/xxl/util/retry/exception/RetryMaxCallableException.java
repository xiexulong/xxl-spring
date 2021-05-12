package com.xxl.util.retry.exception;

public class RetryMaxCallableException extends RetryException {

    public RetryMaxCallableException(String message, Throwable cause) {
        super(message, cause);
    }
}
