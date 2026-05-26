package io.project.paymybuddy.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ModelAttribute
    public void addCurrentUri(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
    }

    @ExceptionHandler(AbstractDomainException.class)
    public ResponseEntity<?> handleAbstractDomainException(final AbstractDomainException domainException) {
        return ResponseEntity.status(domainException.getHttpStatus()).body(domainException.getMessage());
    }
}
