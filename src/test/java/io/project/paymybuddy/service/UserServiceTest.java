package io.project.paymybuddy.service;

import io.project.paymybuddy.dto.UserRegisterDto;
import io.project.paymybuddy.dto.mapper.UserMapper;
import io.project.paymybuddy.event.UserCreatedEvent;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ApplicationEventPublisher publisher;

    @Test
    public void shouldRegisterUser() {
        UserRegisterDto userRegisterDto = new UserRegisterDto("mika", "mika", "mika@test.com");
        User user = new User();
        user.setEmail(userRegisterDto.getEmail());
        user.setUsername(userRegisterDto.getUsername());
        user.setPassword(userRegisterDto.getPassword());

        when(userRepository.findByEmail(userRegisterDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.fromDto(userRegisterDto)).thenReturn(user);
        when(bCryptPasswordEncoder.encode(userRegisterDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        User userCreated = userService.createUser(userRegisterDto);

        verify(userRepository).save(userCreated);
        assertNotNull(userCreated);
        verify(publisher).publishEvent(any(UserCreatedEvent.class));

    }
}
