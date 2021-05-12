package com.xxl.mq.common;

import java.io.IOException;

public class MqException extends IOException {
    
    private static final long serialVersionUID = 7560465878437197272L;

    public MqException(String message) {
        super(message);
    }

    public MqException(String message, Throwable cause) {
        super(message, cause);
    }
}
