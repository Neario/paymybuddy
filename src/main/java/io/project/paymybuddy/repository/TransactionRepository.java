package io.project.paymybuddy.repository;

import io.project.paymybuddy.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllBySenderId(Long senderId);

    @Query("select t from Transaction t where t.sender.id = :id or t.receiver.id = :id order by t.createdAt desc")
    List<Transaction> findAllBySenderOrReceiverIdOrderByCreatedAtDesc(@Param("id") Long userId);
}
