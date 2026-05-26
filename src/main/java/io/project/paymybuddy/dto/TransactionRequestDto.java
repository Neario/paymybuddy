package io.project.paymybuddy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TransactionRequestDto {
    @NotBlank
    private String receiver;

    @NotNull
    @Min(1)
    private int amount;

    @NotBlank
    private String description;

    public TransactionRequestDto() {}

    public TransactionRequestDto(String receiver, int amount, String description) {
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
