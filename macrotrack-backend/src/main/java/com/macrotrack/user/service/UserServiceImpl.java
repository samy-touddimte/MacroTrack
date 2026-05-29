package com.macrotrack.user.service;

import com.macrotrack.user.dto.UpdateUserRequest;
import com.macrotrack.user.dto.UserResponse;
import com.macrotrack.shared.exception.ResourceNotFoundException;
import com.macrotrack.user.model.User;
import com.macrotrack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.Clock;

import com.macrotrack.user.mapper.UserMapper;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserInternalQueryPort {

    private final UserRepository userRepository;
    private final Clock clock;
    private final UserMapper userMapper;

    public UserResponse getCurrentUser(String email) {
        User user = getUserEntityByEmail(email);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateCurrentUser(String email, UpdateUserRequest request) {
        User user = getUserEntityByEmail(email);

        // Age validation handled by @ValidAge on UpdateUserRequest.birthDate
        userMapper.updateEntityFromDto(request, user);

        User updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Override
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
