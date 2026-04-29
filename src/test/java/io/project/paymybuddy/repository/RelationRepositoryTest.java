package io.project.paymybuddy.repository;

import io.project.paymybuddy.model.Relation;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.model.enumeration.UserRole;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RelationRepositoryTest {

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private UserRepository userRepository;

    @ParameterizedTest
    @MethodSource("fareArguments")
    void shouldReturnTrueWhenRelationExists(User userTestOne, User userTestTwo) {
        userRepository.save(userTestOne);
        userRepository.save(userTestTwo);
        Relation relation = new Relation();
        relation.setUser(userTestOne);
        relation.setRelation(userTestTwo);
        relationRepository.save(relation);

        boolean exists = relationRepository.existsByUserIdAndRelationId(userTestOne.getId(), userTestTwo.getId());

        assertThat(exists).isTrue();
    }

    @ParameterizedTest
    @MethodSource("fareArguments")
    void shouldReturnFalseWhenRelationNotExist(User userTestOne, User userTestTwo) {
        boolean exists = relationRepository.existsByUserIdAndRelationId(userTestOne.getId(), userTestTwo.getId());

        assertThat(exists).isFalse();
    }

    @ParameterizedTest
    @MethodSource("fareArguments")
    void shouldFindRelationsByUserId(User userTestOne, User userTestTwo) {
        userRepository.save(userTestOne);
        userRepository.save(userTestTwo);
        Relation relation = new Relation();
        relation.setUser(userTestOne);
        relation.setRelation(userTestTwo);
        relationRepository.save(relation);

        Set<Relation> relations = relationRepository.findByUserId(userTestOne.getId());

        assertThat(relations).hasSize(1);
    }

    @ParameterizedTest
    @MethodSource("fareArguments")
    void shouldReturnEmptyWhenNotRelations(User userTestOne, User userTestTwo) {
        Set<Relation> relations = relationRepository.findByUserId(userTestOne.getId());

        assertThat(relations).isEmpty();
    }

    private static Stream<Arguments> fareArguments() {
        User userTestOne = new User();
        userTestOne.setUsername("mika");
        userTestOne.setEmail("mika@test.com");
        userTestOne.setPassword("mika");
        userTestOne.setRole(UserRole.USER);

        User userTestTwo = new User();
        userTestTwo.setUsername("test");
        userTestTwo.setEmail("test@test.com");
        userTestTwo.setPassword("test");
        userTestTwo.setRole(UserRole.USER);

        return Stream.of(
                Arguments.of(userTestOne, userTestTwo)
        );
    }
}
