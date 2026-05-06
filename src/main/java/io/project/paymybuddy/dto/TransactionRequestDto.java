package io.project.paymybuddy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransactionRequestDto {
    @NotBlank
    private String receiver;

    @NotNull
    @Min(1)
    private BigDecimal amount;

    @NotBlank
    private String description;

    public TransactionRequestDto() {}

    public TransactionRequestDto(String receiver, BigDecimal amount, String description) {
        this.receiver = receiver;
        this.amount = amount;
        this.description = description;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
