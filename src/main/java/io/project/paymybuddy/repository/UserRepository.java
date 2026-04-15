package io.project.paymybuddy.repository;

import io.project.paymybuddy.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Integer> {
    public User findByUsername(String username);
}
