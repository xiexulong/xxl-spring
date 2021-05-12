
package com.xxl.util.retry.exception;

public class RetryInterruptedException extends RetryException {

    public RetryInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
