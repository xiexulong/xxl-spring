
package com.xxl.exception;

/**
 * Created by ypren on 2019/1/17.
 */
public class UrlGroupNotFoundException extends HttpResourceNotFoundException {

    public UrlGroupNotFoundException(String message) {
        super(message);
    }

    public UrlGroupNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
