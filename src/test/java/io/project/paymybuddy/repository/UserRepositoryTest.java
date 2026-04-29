package io.project.paymybuddy.repository;

import io.project.paymybuddy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveUser(){
        User user = new User();
        user.setEmail("mika@mail.com");
        user.setUsername("mika");
        user.setPassword("mika");

        userRepository.save(user);
        assertThat(user.getId()).isNotNull();
    }

}
