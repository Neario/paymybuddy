package io.project.paymybuddy.exception;

import org.springframework.http.HttpStatus;

public class RelationNotFoundException extends AbstractDomainException {

    public RelationNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
