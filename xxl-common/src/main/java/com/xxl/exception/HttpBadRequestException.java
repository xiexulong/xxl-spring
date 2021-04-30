package com.xxl.exception;

public class HttpBadRequestException extends RuntimeException {
    /**
     * Construct a new {@code HttpBadRequestException} with the given message.
     * @param message the message
     */
    public HttpBadRequestException(String message) {
        super(message);
    }

    /**
     * Construct a new {@code HttpBadRequestException} with the given message and {@link Throwable}.
     * @param message the message
     * @param cause the {@code Throwable}
     */
    public HttpBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
