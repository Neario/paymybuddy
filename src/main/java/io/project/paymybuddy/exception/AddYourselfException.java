package io.project.paymybuddy.exception;

import org.springframework.http.HttpStatus;

public class AddYourselfException extends AbstractDomainException{

    public AddYourselfException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
