package com.xxl.exception;

public class UnknownFormatException extends HttpBadRequestException {

    public UnknownFormatException(String message) {
        super(message);
    }

    public UnknownFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
