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
import io.project.paymybuddy.utils.MoneyUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        int amount = MoneyUtils.toCents(transactionRequestDto.getAmount());
        int total = MoneyUtils.withFee(amount, FEE_RATE);

        final Wallet senderWallet = sender.getWallet();


        if (senderWallet.getBalance() < total) {
            throw new InsufficientSoldException("Insufficient sold in your wallet", HttpStatus.CONFLICT);
        }

        final Wallet receiverWallet = receiver.getWallet();

        senderWallet.setBalance(senderWallet.getBalance() - total);
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);

        walletRepository.saveAll(List.of(senderWallet, receiverWallet));

        // todo : passer par un constructeur
        final Transaction transaction = new Transaction();
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


    public List<TransactionResponse> getAllTransactions(long senderId) {

        return transactionRepository.findAllBySenderOrReceiverId(senderId)
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
                transaction.getAmount() * multiplicator,
                LocalDateTime.now() //todo fix
        );
    }

}
