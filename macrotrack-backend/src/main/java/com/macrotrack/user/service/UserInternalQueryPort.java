package com.macrotrack.user.service;

import com.macrotrack.user.model.User;

public interface UserInternalQueryPort {
    User getUserEntityByEmail(String email);
    User getUserEntityById(Long id);
}
