package com.macrotrack.auth.event;

import com.macrotrack.user.model.User;
import com.macrotrack.auth.dto.RegisterRequest;
import org.springframework.context.ApplicationEvent;

public class UserRegisteredEvent extends ApplicationEvent {
    
    private final User user;
    private final RegisterRequest registerRequest;

    public UserRegisteredEvent(Object source, User user, RegisterRequest registerRequest) {
        super(source);
        this.user = user;
        this.registerRequest = registerRequest;
    }

    public User getUser() {
        return user;
    }

    public RegisterRequest getRegisterRequest() {
        return registerRequest;
    }
}
