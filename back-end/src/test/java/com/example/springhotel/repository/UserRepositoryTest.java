package com.example.springhotel.repository;

import com.example.springhotel.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Test pour saugarder un utilisateur et le retourvé par email")
    void shouldSaveAndFindByEmail() {
        User user = new User();
        user.setFirstName("Malik");
        user.setLastName("Ibo");
        user.setEmail("malik@test.com");
        user.setPassword("password");
        user.setEnabled(true);

        userRepository.save(user);

        Optional<User> userFromDb = userRepository.findByEmail("malik@test.com");

        assertThat(userFromDb).isPresent();
        assertThat(userFromDb.get().getEmail()).isEqualTo("malik@test.com");
        assertThat(userFromDb.get().getFirstName()).isEqualTo("Malik");
        assertThat(userFromDb.get().isEnabled()).isTrue();
    }
}