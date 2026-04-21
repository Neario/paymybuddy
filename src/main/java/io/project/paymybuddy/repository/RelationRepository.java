package io.project.paymybuddy.repository;

import io.project.paymybuddy.model.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationRepository extends JpaRepository<Relation, Integer> {
}
