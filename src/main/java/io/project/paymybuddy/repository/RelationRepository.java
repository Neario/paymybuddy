package io.project.paymybuddy.repository;

import io.project.paymybuddy.model.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RelationRepository extends JpaRepository<Relation, Integer> {
    boolean existsByUserIdAndRelationId(Long userId, Long relationId);
    Set<Relation> findByUserId(Long userId);
}
