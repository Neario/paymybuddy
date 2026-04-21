package io.project.paymybuddy.event;

import io.project.paymybuddy.model.User;
import org.springframework.context.ApplicationEvent;

public class UserCreatedEvent extends ApplicationEvent {
    private final User user;

    public UserCreatedEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
    public User getUser() {
        return user;
    }
}
