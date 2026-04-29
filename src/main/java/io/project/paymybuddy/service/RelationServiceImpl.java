package io.project.paymybuddy.service;

import io.project.paymybuddy.dto.RelationDto;
import io.project.paymybuddy.exception.AddYourselfException;
import io.project.paymybuddy.exception.AlreadyExistsException;
import io.project.paymybuddy.exception.UserNotFoundException;
import io.project.paymybuddy.model.Relation;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.repository.RelationRepository;
import io.project.paymybuddy.repository.UserRepository;
import io.project.paymybuddy.service.interfaces.RelationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class RelationServiceImpl implements RelationService {

    private final RelationRepository relationRepository;
    private final UserRepository userRepository;

    public RelationServiceImpl(RelationRepository relationRepository, UserRepository userRepository) {
        this.relationRepository = relationRepository;
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public void addRelation(User currentUser, RelationDto relationDto) {
        User contact = userRepository.findByEmail(relationDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("No user found with email", HttpStatus.NOT_FOUND));

        if (currentUser.getId().equals(contact.getId())) {
            throw new AddYourselfException("Cannot add yourself", HttpStatus.CONFLICT);
        }
        if (relationRepository.existsByUserIdAndRelationId(currentUser.getId(), contact.getId())) {
            throw new AlreadyExistsException("User already in your contact", HttpStatus.CONFLICT);
        }
        Relation relation = new Relation();
        relation.setUser(currentUser);
        relation.setRelation(contact);
        relationRepository.save(relation);
    }

    @Override
    public Set<Relation> getRelations(Long userId) {
        return relationRepository.findByUserId(userId);
    }
}
