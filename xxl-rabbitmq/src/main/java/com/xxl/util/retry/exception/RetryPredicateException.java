package com.xxl.util.retry.exception;

public class RetryPredicateException extends RetryException {

    private final Object result;

    public RetryPredicateException(Object result, String message) {
        super(message);
        this.result = result;
    }

    public RetryPredicateException(Object result, String message, Throwable cause) {
        super(message, cause);
        this.result = result;
    }

    public Object getResult() {
        return result;
    }
}
