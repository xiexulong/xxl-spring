package com.xxl.exception;

@SuppressWarnings("serial")
public class HttpResourceNotFoundException extends RuntimeException {

    /**
     * Construct a new {@code HttpResourceNotFoundException} with the given message.
     * @param message the message
     */
    public HttpResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Construct a new {@code HttpResourceNotFoundException} with the given message and {@link Throwable}.
     * @param message the message
     * @param cause the {@code Throwable}
     */
    public HttpResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
