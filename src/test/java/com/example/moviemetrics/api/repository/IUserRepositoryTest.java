package com.example.moviemetrics.api.repository;

import com.example.moviemetrics.api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IUserRepositoryTest {

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    public void itShouldCheckUserExistsByEmail() {
        // given
        String email = "test@test.com";

        User user = new User(email, "test", "test");

        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmail(email);

        // then
        assertTrue(found.isPresent());
        assertInstanceOf(User.class, found.get());
    }

    @Test
    public void itShouldCheckMovieDoesNotExistByTitle() {
        // given
        String email = "test@test.com";

        // when
        Optional<User> found = userRepository.findByEmail(email);

        // then
        assertTrue(found.isEmpty());
    }
}
