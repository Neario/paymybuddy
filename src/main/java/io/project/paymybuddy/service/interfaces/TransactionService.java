package io.project.paymybuddy.service.interfaces;

import io.project.paymybuddy.dto.TransactionRequestDto;
import io.project.paymybuddy.dto.TransactionResponse;
import io.project.paymybuddy.model.Transaction;
import io.project.paymybuddy.model.User;

import java.util.List;

public interface TransactionService {
    void transaction(User CurrentUser, TransactionRequestDto transactionRequestDto);
    List<Transaction> getTransactions(long userId);
    List<TransactionResponse> getAllTransactions(long senderId);
}
