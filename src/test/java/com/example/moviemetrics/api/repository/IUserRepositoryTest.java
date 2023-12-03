package com.example.moviemetrics.api.repository;

import com.example.moviemetrics.api.model.ERole;
import com.example.moviemetrics.api.model.Movie;
import com.example.moviemetrics.api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class IUserRepositoryTest {

    @Autowired
    IUserRepository userRepository;

    @Test
    void itShouldCheckIfUserExistsByEmail() {
        // given
        String email = "test@test.com";
        User user = User.builder().email(email).password("test").ERole(ERole.USER).build();
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmail(email);

        // then
        assertThat(found.isPresent()).isTrue();
    }

    @Test
    void itShouldCheckIfUserDoesNotExistByEmail() {
        // given
        String email = "test@test.com";

        // when
        Optional<User> found = userRepository.findByEmail(email);

        // then
        assertThat(found.isEmpty()).isTrue();
    }
}