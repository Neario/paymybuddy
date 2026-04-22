package io.project.paymybuddy.service;

import io.project.paymybuddy.dto.UserRegisterDto;
import io.project.paymybuddy.dto.mapper.UserMapper;
import io.project.paymybuddy.event.UserCreatedEvent;
import io.project.paymybuddy.exception.AlreadyExistsException;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ApplicationEventPublisher publisher;

    @ParameterizedTest
    @MethodSource("fareArguments")
    public void shouldRegisterUser(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setEmail(userRegisterDto.getEmail());
        user.setUsername(userRegisterDto.getUsername());
        user.setPassword(userRegisterDto.getPassword());

        when(userRepository.findByEmail(userRegisterDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.fromDto(userRegisterDto)).thenReturn(user);
        when(passwordEncoder.encode(userRegisterDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        User userCreated = userService.createUser(userRegisterDto);

        verify(userRepository).save(any(User.class));
        assertNotNull(userCreated);
        verify(publisher).publishEvent(any(UserCreatedEvent.class));
    }

    @ParameterizedTest
    @MethodSource("fareArguments")
    public void shouldThrowExceptionWhenEmailAlreadyExists(UserRegisterDto userRegisterDto) {
        when(userRepository.findByEmail(userRegisterDto.getEmail())).thenReturn(Optional.of(new User()));
        assertThrows(AlreadyExistsException.class, () -> {
            userService.createUser(userRegisterDto);
        });
    }

    private static Stream<Arguments> fareArguments() {
        UserRegisterDto userRegisterDto = new UserRegisterDto("mika", "mika", "mika@test.com");
        return Stream.of(
                Arguments.of(userRegisterDto)
        );
    }
}
