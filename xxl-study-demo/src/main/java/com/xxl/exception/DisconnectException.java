package com.xxl.exception;

public class DisconnectException extends RuntimeException {

    /**
     * Construct a new {@code DisconnectException} with the given message.
     * @param message the message
     */
    public DisconnectException(String message) {
        super(message);
    }

    /**
     * Construct a new {@code DisconnectException} with the given message and {@link Throwable}.
     * @param message the message
     * @param cause the {@code Throwable}
     */
    public DisconnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
