package io.project.paymybuddy.service;

import io.project.paymybuddy.dto.UserRegisterDto;
import io.project.paymybuddy.dto.mapper.UserMapper;
import io.project.paymybuddy.event.UserCreatedEvent;
import io.project.paymybuddy.exception.AlreadyExistsException;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.repository.UserRepository;
import io.project.paymybuddy.service.interfaces.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.publisher = applicationEventPublisher;
    }

    @Override
    public User createUser(UserRegisterDto userRegisterDto) {
        boolean existUser = userRepository.findByEmail(userRegisterDto.getEmail()).isPresent();
        if (existUser) {
            throw new AlreadyExistsException("Email already exist", HttpStatus.CONFLICT);
        }
        User user = userMapper.fromDto(userRegisterDto);
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        User savedUser = userRepository.save(user);
        publisher.publishEvent(new UserCreatedEvent(this, savedUser));
        return savedUser;
    }
}
