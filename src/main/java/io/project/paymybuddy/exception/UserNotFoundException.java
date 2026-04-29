package io.project.paymybuddy.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AbstractDomainException{

    public UserNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
