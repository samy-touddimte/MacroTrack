package com.macrotrack.user.repository;

import com.macrotrack.AbstractIntegrationTest;
import com.macrotrack.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {
        // Arrange
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .passwordHash("hashedpassword")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }
}
