package io.project.paymybuddy.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AbstractDomainException.class)
    public ResponseEntity<?> handleAbstractDomainException(final AbstractDomainException domainException) {
        return ResponseEntity.status(domainException.getHttpStatus()).body(domainException.getMessage());
    }
}
