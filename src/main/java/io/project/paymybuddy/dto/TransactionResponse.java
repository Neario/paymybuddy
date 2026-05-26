package io.project.paymybuddy.dto;

import java.time.LocalDateTime;

public record TransactionResponse(
        String user,
        String description,
        Integer amount,
        LocalDateTime timestamp

) {

}
