package io.project.paymybuddy.dto;

import java.time.LocalDateTime;

public record TransactionResponse(
        String user,
        Integer amount,
        LocalDateTime timestamp

) {

}
