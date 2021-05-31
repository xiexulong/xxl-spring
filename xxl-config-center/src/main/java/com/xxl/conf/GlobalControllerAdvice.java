package com.xxl.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ExceptionHandler({
            IllegalArgumentException.class, // general argument error
            MethodArgumentNotValidException.class // jackson object validation failed in controller method
    })
    public void badRequest(Exception e, HttpServletResponse response) throws IOException {
        logger.error("bad request", e);
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({
            IncorrectUpdateSemanticsDataAccessException.class //sql update row number Incorrect.
    })
    public void expectationFailed(Exception e, HttpServletResponse response) throws IOException {
        logger.error("expectation failed", e);
        response.sendError(HttpStatus.EXPECTATION_FAILED.value());
    }


}
