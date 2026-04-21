package io.project.paymybuddy.service.interfaces;

import io.project.paymybuddy.dto.UserRegisterDto;
import io.project.paymybuddy.model.User;

public interface UserService {
    User createUser(UserRegisterDto userRegisterDto);
}
