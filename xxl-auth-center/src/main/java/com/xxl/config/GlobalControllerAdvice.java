package com.xxl.config;

import com.xxl.exception.HttpPermissionDenyException;
import com.xxl.exception.HttpResourceNotFoundException;
import com.xxl.exception.HttpUnauthorizedException;
import com.xxl.exception.RoleAlreadyAssignedException;
import com.xxl.exception.UnknownFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class GlobalControllerAdvice {



    private static final Logger logger = LoggerFactory.getLogger(GlobalControllerAdvice.class);


    @ExceptionHandler(Exception.class)
    public void handleOtherException(Exception e, HttpServletResponse response) throws IOException {
        logger.error("handleOtherException", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        String message = "Oops, we got one problem! Please contact support!";
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),message);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(Exception e, HttpServletResponse response) throws IOException   {
        logger.error("handleIllegalArgumentException", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        String message = "Oops, we got one problem! Please contact support!";
        response.sendError(HttpStatus.UNAUTHORIZED.value(),message);

    }



    /**
     * this is a Collection of exceptions of code 400.
     * @param e e.
     * @param response response.
     * @throws IOException IOException.
     */
    @ExceptionHandler({DataIntegrityViolationException.class})
    public void handleDataIntegrityViolationException(Exception e, HttpServletResponse response) throws IOException  {
        logger.error("handleDataIntegrityViolationException", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String body;
        if (e.toString().contains("ix_display_name")) {
            body = "Display Name existed!";
        } else if (e.toString().contains("ix_username")) {
            body = "User Name existed!";
        } else if (e.toString().contains("fk_authorities_type")) {
            body = "Role has been assigned to users!";
        } else if (e.toString().contains("ix_roles_role")) {
            body = "Role Name existed!";
        } else {
            body = "Oops, we got one problem! Please contact support!";
        }

        response.sendError(HttpStatus.BAD_REQUEST.value(),body);
        //return new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
    }




    @ExceptionHandler({
            UnknownFormatException.class, RoleAlreadyAssignedException.class
    })
    public void roleAlreadyAssignedException(Exception e, HttpServletResponse response) throws IOException {
        logger.error("BAD_REQUEST ", e);
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }



    @ExceptionHandler({
            HttpUnauthorizedException.class
    })
    public void unauthorized(Exception e, HttpServletResponse response) throws IOException {
        logger.error("Unauthorized ", e);
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler({
            HttpPermissionDenyException.class
    })
    public void forbidden(Exception e, HttpServletResponse response) throws IOException {
        logger.error("Permission Deny", e);
        response.sendError(HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public void argumentNotValid(Exception e, HttpServletResponse response) throws IOException {
        logger.error("ArgumentNotValid", e);
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }


    @ExceptionHandler({
            HttpResourceNotFoundException.class, // general not-found error
    })
    public void notFound(Exception e, HttpServletResponse response) throws IOException {
        logger.error("not found", e);
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

}
