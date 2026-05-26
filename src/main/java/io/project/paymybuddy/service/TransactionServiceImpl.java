package io.project.paymybuddy.service;

import io.project.paymybuddy.dto.TransactionRequestDto;
import io.project.paymybuddy.dto.TransactionResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.005");

    private final UserRepository userRepository;
    private final RelationRepository relationRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(final UserRepository userRepository,
                                  final RelationRepository relationRepository,
                                  final WalletRepository walletRepository,
                                  final TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.relationRepository = relationRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void transaction(final User sender, final TransactionRequestDto transactionRequestDto) {

        final User receiver = userRepository.findByEmail(transactionRequestDto.getReceiver())
                .orElseThrow(() -> new UserNotFoundException("No user found with email", HttpStatus.NOT_FOUND));

        if (receiver.getId().equals(sender.getId())) {
            throw new SendMoneyYourselfException("Cannot send money yourself", HttpStatus.CONFLICT);
        }

        if (!relationRepository.existsByUserIdAndRelationId(sender.getId(), receiver.getId())) {
            throw new RelationNotFoundException("You can only send money to your contacts", HttpStatus.NOT_FOUND);
        }

        int amount = transactionRequestDto.getAmount() * 100;
        int fee = BigDecimal.valueOf(amount).multiply(FEE_RATE).setScale(0, RoundingMode.HALF_UP).intValueExact();
        int total = amount + fee;

        final Wallet senderWallet = sender.getWallet();

        if (senderWallet.getBalance() < total) {
            throw new InsufficientSoldException("Insufficient sold in your wallet", HttpStatus.CONFLICT);
        }

        final Wallet receiverWallet = receiver.getWallet();

        senderWallet.setBalance(senderWallet.getBalance() - total);
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);

        walletRepository.saveAll(List.of(senderWallet, receiverWallet));

        final Transaction transaction = new Transaction(sender, receiver, transactionRequestDto.getDescription(), amount, fee);
        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactions(long senderId) {
        return transactionRepository.findAllBySenderId(senderId);
    }


    public List<TransactionResponse> getAllTransactions(long senderId) {

        return transactionRepository.findAllBySenderOrReceiverIdOrderByCreatedAtDesc(senderId)
                .stream()
                .map(transaction -> convertTransaction(transaction, senderId))
                .toList();

    }

    private TransactionResponse convertTransaction(final Transaction transaction, final Long userId) {
        int multiplicator = 1;
        String user;

        if (transaction.getSender().getId().equals(userId)) {
            multiplicator = -1;
            user = transaction.getReceiver().getUsername();
        } else {
            user = transaction.getSender().getUsername();
        }

        return new TransactionResponse(
                user,
                transaction.getDescription(),
                transaction.getAmount() * multiplicator,
                transaction.getCreatedAt()
        );
    }

}
