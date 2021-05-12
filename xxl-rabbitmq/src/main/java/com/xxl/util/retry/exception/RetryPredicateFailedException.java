package com.xxl.util.retry.exception;

public class RetryPredicateFailedException extends RetryPredicateException {

    public RetryPredicateFailedException(Object result, String message, Throwable cause) {
        super(result, message, cause);
    }
}
