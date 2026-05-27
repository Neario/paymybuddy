package io.project.paymybuddy.service;

import io.project.paymybuddy.dto.TransactionRequestDto;
import io.project.paymybuddy.exception.*;
import io.project.paymybuddy.model.Transaction;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.model.Wallet;
import io.project.paymybuddy.model.enumeration.UserRole;
import io.project.paymybuddy.repository.RelationRepository;
import io.project.paymybuddy.repository.TransactionRepository;
import io.project.paymybuddy.repository.UserRepository;
import io.project.paymybuddy.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RelationRepository relationRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User sender;
    private User receiver;
    private Wallet senderWallet;
    private Wallet receiverWallet;
    private TransactionRequestDto  transactionRequestDto;

    @BeforeEach
    public void setup() {
        sender = new User();
        sender.setId(1L);
        sender.setUsername("mika");
        sender.setEmail("mika@test.com");
        sender.setPassword("mika");
        sender.setRole(UserRole.USER);

        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("test");
        receiver.setEmail("test@test.com");
        receiver.setPassword("test");
        receiver.setRole(UserRole.USER);

        senderWallet = new Wallet();
        senderWallet.setUser(sender);
        senderWallet.setBalance(10000);
        sender.setWallet(senderWallet);

        receiverWallet = new Wallet();
        receiverWallet.setUser(receiver);
        receiverWallet.setBalance(0);
        receiver.setWallet(receiverWallet);

        transactionRequestDto = new TransactionRequestDto("test@test.com", 10, "test");
    }

    @Test
    public void shouldTransactionSuccess() {
        //GIVEN
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(receiver));
        when(relationRepository.existsByUserIdAndRelationId(1L, 2L)).thenReturn(true);

        //WHEN
        transactionService.transaction(sender, transactionRequestDto);

        //THEN
        Assertions.assertEquals(8995, senderWallet.getBalance());
        Assertions.assertEquals(1000, receiverWallet.getBalance());
    }

    @Test
    public void shouldTransactionReturnUserNotFoundWhenReceiverNotFound() {
        when(userRepository.findByEmail(transactionRequestDto.getReceiver())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            transactionService.transaction(sender, transactionRequestDto);
        });
    }

    @Test
    public void shouldTransactionReturnSendMoneyYourselfException() {
        TransactionRequestDto transactionRequestSendYourselfDto = new TransactionRequestDto("mika@test.com"
                , 10
                , "test");
        when(userRepository.findByEmail(transactionRequestSendYourselfDto.getReceiver())).thenReturn(Optional.of(sender));
        assertThrows(SendMoneyYourselfException.class, () -> {
            transactionService.transaction(sender, transactionRequestSendYourselfDto);
        });

    }

    @Test
    public void shouldTransactionReturnNotFoundWhenReceiverNotInRelation() {
        when(userRepository.findByEmail(transactionRequestDto.getReceiver())).thenReturn(Optional.of(receiver));
        when(relationRepository.existsByUserIdAndRelationId(1L, 2L)).thenReturn(false);
        assertThrows(RelationNotFoundException.class, () -> {
            transactionService.transaction(sender, transactionRequestDto);
        });
    }

    @Test
    public void shouldTransactionReturnInsufficientSoldException() {
        senderWallet.setBalance(0);
        when(userRepository.findByEmail(transactionRequestDto.getReceiver())).thenReturn(Optional.of(receiver));
        when(relationRepository.existsByUserIdAndRelationId(1L, 2L)).thenReturn(true);

        assertThrows(InsufficientSoldException.class, () -> {
            transactionService.transaction(sender, transactionRequestDto);
        });
    }

    @Test
    public void shouldReturnTransactions() {
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setDescription("test");
        transaction.setAmount(1000);

        when(transactionRepository.findAllBySenderId(sender.getId())).thenReturn(List.of(transaction));

        List<Transaction> result = transactionService.getTransactions(sender.getId());

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void shouldReturnEmptyListWhenNoTransactionsFound() {
        when(transactionRepository.findAllBySenderId(sender.getId())).thenReturn(List.of());

        List<Transaction> result = transactionService.getTransactions(sender.getId());

        Assertions.assertTrue(result.isEmpty());
    }
}
