package com.xxl.exception;

public class RoleAlreadyAssignedException extends HttpBadRequestException {

    public RoleAlreadyAssignedException(String message) {
        super(message);
    }

    public RoleAlreadyAssignedException(String message, Throwable cause) {
        super(message, cause);
    }
}
