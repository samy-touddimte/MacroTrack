package com.macrotrack.user.service;

import com.macrotrack.user.dto.UpdateUserRequest;
import com.macrotrack.user.dto.UserResponse;
import com.macrotrack.user.model.User;

public interface UserService {
    UserResponse getCurrentUser(String email);
    UserResponse updateCurrentUser(String email, UpdateUserRequest request);
}
