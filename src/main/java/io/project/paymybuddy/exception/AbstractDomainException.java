package io.project.paymybuddy.exception;

import org.springframework.http.HttpStatus;

public class AbstractDomainException extends RuntimeException {

    protected HttpStatus httpStatus;

    protected AbstractDomainException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
