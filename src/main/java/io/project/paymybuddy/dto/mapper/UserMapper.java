package io.project.paymybuddy.dto.mapper;

import io.project.paymybuddy.dto.UserRegisterDto;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.model.enumeration.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User fromDto(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setUsername(userRegisterDto.getUsername());
        user.setPassword(userRegisterDto.getPassword());
        user.setEmail(userRegisterDto.getEmail());
        user.setRole(UserRole.USER);
        return user;
    }
}
