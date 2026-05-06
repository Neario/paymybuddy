package io.project.paymybuddy.exception;

import org.springframework.http.HttpStatus;

public class InsufficientSoldException extends AbstractDomainException {

    public InsufficientSoldException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
