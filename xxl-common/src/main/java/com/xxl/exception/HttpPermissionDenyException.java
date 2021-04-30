package com.xxl.exception;

public class HttpPermissionDenyException extends RuntimeException  {
    /**
     * Construct a new {@code HttpResourceNotFoundException} with the given message.
     * @param message the message
     */
    public HttpPermissionDenyException(String message) {
        super(message);
    }

    /**
     * Construct a new {@code HttpResourceNotFoundException} with the given message and {@link Throwable}.
     * @param message the message
     * @param cause the {@code Throwable}
     */
    public HttpPermissionDenyException(String message, Throwable cause) {
        super(message, cause);
    }
}
