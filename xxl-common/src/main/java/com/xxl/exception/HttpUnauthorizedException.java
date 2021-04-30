package com.xxl.exception;

public class HttpUnauthorizedException extends RuntimeException  {
    /**
     * Construct a new {@code HttpResourceNotFoundException} with the given message.
     * @param message the message
     */
    public HttpUnauthorizedException(String message) {
        super(message);
    }

    /**
     * Construct a new {@code HttpResourceNotFoundException} with the given message and {@link Throwable}.
     * @param message the message
     * @param cause the {@code Throwable}
     */
    public HttpUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

}
