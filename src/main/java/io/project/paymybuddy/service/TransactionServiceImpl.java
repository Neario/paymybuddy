package io.project.paymybuddy.service;

import io.project.paymybuddy.dto.TransactionRequestDto;
import io.project.paymybuddy.exception.InsufficientSoldException;
import io.project.paymybuddy.exception.RelationNotFoundException;
import io.project.paymybuddy.exception.SendMoneyYourselfException;
import io.project.paymybuddy.exception.UserNotFoundException;
import io.project.paymybuddy.model.Transaction;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.model.Wallet;
import io.project.paymybuddy.repository.RelationRepository;
import io.project.paymybuddy.repository.TransactionRepository;
import io.project.paymybuddy.repository.UserRepository;
import io.project.paymybuddy.repository.WalletRepository;
import io.project.paymybuddy.service.interfaces.TransactionService;
import io.project.paymybuddy.utils.MoneyUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.005");

    private final UserRepository userRepository;
    private final RelationRepository relationRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(UserRepository userRepository, RelationRepository relationRepository,
                                  WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.relationRepository = relationRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void transaction(User sender, TransactionRequestDto transactionRequestDto) {
        User receiver = userRepository.findByEmail(transactionRequestDto.getReceiver())
                .orElseThrow(() -> new UserNotFoundException("No user found with email", HttpStatus.NOT_FOUND));
        Wallet senderWallet = sender.getWallet();
        Wallet receiverWallet = receiver.getWallet();

        if (receiver.getId().equals(sender.getId())) {
            throw new SendMoneyYourselfException("Cannot send money yourself", HttpStatus.CONFLICT);
        }

        if (!relationRepository.existsByUserIdAndRelationId(sender.getId(), receiver.getId())) {
            throw new RelationNotFoundException("You can only send money to your contacts", HttpStatus.NOT_FOUND);
        }

        int amount = MoneyUtils.toCents(transactionRequestDto.getAmount());
        int total = MoneyUtils.withFee(amount, FEE_RATE);

        if (senderWallet.getBalance() < total) {
            throw new InsufficientSoldException("Insufficient sold in your wallet", HttpStatus.CONFLICT);
        }

        senderWallet.setBalance(senderWallet.getBalance() - total);
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(transactionRequestDto.getDescription());
        transaction.setFee(FEE_RATE);
        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactions(long senderId) {
        return transactionRepository.findAllBySenderId(senderId);
    }
}
