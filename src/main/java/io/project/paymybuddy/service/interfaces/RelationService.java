package io.project.paymybuddy.service.interfaces;

import io.project.paymybuddy.dto.RelationDto;
import io.project.paymybuddy.model.Relation;
import io.project.paymybuddy.model.User;

import java.util.Set;

public interface RelationService {
    void addRelation(User currentUser, RelationDto relationDto);
    Set<Relation> getRelations(Long userId);
}
