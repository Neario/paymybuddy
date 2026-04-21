package io.project.paymybuddy.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends AbstractDomainException{

    public AlreadyExistsException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
