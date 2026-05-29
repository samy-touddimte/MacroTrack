package com.macrotrack.user.controller;

import com.macrotrack.user.model.User;

import com.macrotrack.user.dto.UpdateUserRequest;
import com.macrotrack.user.dto.UserResponse;
import com.macrotrack.user.service.UserService;
import com.macrotrack.auth.security.CurrentUserEmail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "User profile management")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public UserResponse getCurrentUser(@CurrentUserEmail String email) {
        return userService.getCurrentUser(email);
    }

    @PatchMapping("/me")
    @Operation(summary = "Update current user profile")
    public UserResponse updateCurrentUser(@CurrentUserEmail String email, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateCurrentUser(email, request);
    }
}
