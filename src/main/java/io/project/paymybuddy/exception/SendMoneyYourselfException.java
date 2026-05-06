package io.project.paymybuddy.exception;

import org.springframework.http.HttpStatus;

public class SendMoneyYourselfException extends AbstractDomainException {

    public SendMoneyYourselfException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
