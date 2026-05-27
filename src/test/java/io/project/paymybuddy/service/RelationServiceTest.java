package io.project.paymybuddy.service;

import io.project.paymybuddy.dto.RelationDto;
import io.project.paymybuddy.exception.AddYourselfException;
import io.project.paymybuddy.exception.UserNotFoundException;
import io.project.paymybuddy.model.Relation;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.model.enumeration.UserRole;
import io.project.paymybuddy.repository.RelationRepository;
import io.project.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RelationServiceTest {
    @Mock
    private RelationRepository relationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RelationServiceImpl relationService;

    private User currentUser;
    private User contact;
    private RelationDto relationDto;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("Mika");
        currentUser.setEmail("mika@test.com");
        currentUser.setPassword("mika");
        currentUser.setRole(UserRole.USER);

        contact = new User();
        contact.setId(2L);
        contact.setUsername("test");
        contact.setEmail("test@test.com");
        contact.setPassword("test");
        contact.setRole(UserRole.USER);

        relationDto = new RelationDto("test@test.com");
    }

    @Test
    void shouldAddRelation() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(contact));

        relationService.addRelation(currentUser, relationDto);

        verify(relationRepository, times(1)).save(any(Relation.class));
    }

    @Test
    void shouldThrowUserNotFoundWhenEmailNotExist() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            relationService.addRelation(currentUser, relationDto);
        });

        verify(relationRepository, never()).save(any());
    }

    @Test
    void shouldThrowAddYourselfExceptionWhenAddingSelf() {
        RelationDto relationDtoMySelf = new RelationDto("mika@test.com");
        when(userRepository.findByEmail("mika@test.com")).thenReturn(Optional.of(currentUser));

        assertThrows(AddYourselfException.class, () -> {
            relationService.addRelation(currentUser, relationDtoMySelf);
        });

        verify(relationRepository, never()).save(any());
    }

}
