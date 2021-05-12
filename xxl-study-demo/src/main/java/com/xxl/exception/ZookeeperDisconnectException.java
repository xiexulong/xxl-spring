package com.xxl.exception;

public class ZookeeperDisconnectException extends DisconnectException {
    /**
     * Construct a new {@code ZookeeperDisconnectException} with the given message.
     * @param message the message
     */
    public ZookeeperDisconnectException(String message) {
        super(message);
    }

    /**
     * Construct a new {@code ZookeeperDisconnectException} with the given message and {@link Throwable}.
     * @param message the message
     * @param cause the {@code Throwable}
     */
    public ZookeeperDisconnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
