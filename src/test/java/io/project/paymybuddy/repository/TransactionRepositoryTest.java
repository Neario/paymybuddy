package io.project.paymybuddy.repository;

import io.project.paymybuddy.model.Transaction;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.model.Wallet;
import io.project.paymybuddy.model.enumeration.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User sender;
    private User receiver;

    @BeforeEach
    void setup(){
        sender = new User();
        sender.setUsername("mika");
        sender.setEmail("mika@test.com");
        sender.setPassword("mika");
        sender.setRole(UserRole.USER);
        userRepository.save(sender);

        receiver = new User();
        receiver.setUsername("test");
        receiver.setEmail("test@test.com");
        receiver.setPassword("test");
        receiver.setRole(UserRole.USER);
        userRepository.save(receiver);

        Wallet senderWallet = new Wallet();
        senderWallet.setUser(sender);
        senderWallet.setBalance(10000);
        walletRepository.save(senderWallet);

        Wallet receiverWallet = new Wallet();
        receiverWallet.setUser(receiver);
        receiverWallet.setBalance(0);
        walletRepository.save(receiverWallet);
    }

    @Test
    void shouldFindTransactionsBySenderId() {
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(1000);
        transaction.setDescription("Test");
        transaction.setFee(new BigDecimal("0.005"));
        transactionRepository.save(transaction);

        List<Transaction> result = transactionRepository.findAllBySenderId(sender.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getReceiver().getEmail()).isEqualTo("test@test.com");
        assertThat(result.getFirst().getAmount()).isEqualTo(1000);
    }
}
