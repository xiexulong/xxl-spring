package com.xxl.util.retry.exception;

public class RetryPredicateFalseException extends RetryPredicateException {

    public RetryPredicateFalseException(Object result, String message) {
        super(result, message);
    }
}
