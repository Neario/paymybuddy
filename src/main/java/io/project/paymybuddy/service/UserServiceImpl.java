package io.project.paymybuddy.service;

import io.project.paymybuddy.dto.UserRegisterDto;
import io.project.paymybuddy.dto.mapper.UserMapper;
import io.project.paymybuddy.exception.AlreadyExistsException;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.repository.UserRepository;
import io.project.paymybuddy.service.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User createUser(UserRegisterDto userRegisterDto) {
        boolean existUser = userRepository.findByEmail(userRegisterDto.getEmail()).isPresent();
        if (existUser) {
            throw new AlreadyExistsException("Email already exist", HttpStatus.CONFLICT);
        }
        User user = userMapper.fromDto(userRegisterDto);
        user.setPassword(bCryptPasswordEncoder.encode(userRegisterDto.getPassword()));
        return userRepository.save(user);
    }
}
