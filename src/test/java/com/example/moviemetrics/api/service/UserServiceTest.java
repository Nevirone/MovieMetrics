package com.example.moviemetrics.api.service;

import com.example.moviemetrics.api.DTO.UserDto;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.ERole;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.repository.IUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class UserServiceTest {
    private AutoCloseable autoCloseable;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private IUserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canAddUser() {
        // given
        UserDto userDto = UserDto.builder().email("test@test.com").password("test").isPasswordEncrypted(true).isAdmin(false).build();

        // when
        userService.createUser(userDto);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(capturedUser.getPassword()).isEqualTo(userDto.getPassword());
    }

    @Test
    void addingUserWillThrowWhenEmailIsTaken() {
        // given
        UserDto userDto = UserDto.builder().email("test@test.com").password("test").isPasswordEncrypted(true).isAdmin(false).build();

        given(userRepository.findByEmail(userDto.getEmail())).willReturn(Optional.of(User.builder().build()));

        // when
        // then
        assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining("Email")
                .hasMessageContaining(userDto.getEmail())
                .hasMessageContaining("is taken");
    }

    @Test
    void canGetUserById() {
        // given
        Long id = 2L;
        Optional<User> userOptional = Optional.of(
                User.builder()
                        .email("test@test.com")
                        .password("test")
                        .ERole(ERole.USER)
                        .build()
        );

        given(userRepository.findById(id)).willReturn(userOptional);

        // when
        User found = userService.getUserById(id);

        // then
        assertThat(userOptional.get().getEmail()).isEqualTo(found.getEmail());
        assertThat(userOptional.get().getPassword()).isEqualTo(found.getPassword());
        assertThat(userOptional.get().getERole()).isEqualTo(found.getERole());
    }

    @Test
    void gettingUserByIdWillThrowWhenUserIsNotFound() {
        // given
        Long id = 2L;

        given(userRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id")
                .hasMessageContaining(id.toString())
                .hasMessageContaining("not found");
    }

    @Test
    void canGetUserByEmail() {
        // given

        Optional<User> userOptional = Optional.of(
                User.builder()
                        .email("test@test.com")
                        .password("test")
                        .ERole(ERole.USER)
                        .build()
        );

        given(userRepository.findByEmail(userOptional.get().getEmail())).willReturn(userOptional);

        // when
        User found = userService.getUserByEmail(userOptional.get().getEmail());

        // then
        assertThat(userOptional.get().getEmail()).isEqualTo(found.getEmail());
        assertThat(userOptional.get().getPassword()).isEqualTo(found.getPassword());
        assertThat(userOptional.get().getERole()).isEqualTo(found.getERole());
    }

    @Test
    void gettingUserByEmailWillThrowWhenUserIsNotFound() {
        // given
        String email = "test@test.com";

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with email")
                .hasMessageContaining(email)
                .hasMessageContaining("not found");
    }

    @Test
    void getAllUsers() {
        // when
        userService.getAllUsers();

        // then
        verify(userRepository).findAll();
    }

    @Test
    void canUpdateUser() {
        // given
        Long id = 2L;
        UserDto userDto = UserDto.builder().email("test@test.com").password("test").isPasswordEncrypted(true).isAdmin(false).build();

        given(userRepository.findById(2L)).willReturn(Optional.of(User.builder().id(id).build()));
        given(userRepository.findByEmail(userDto.getEmail())).willReturn(Optional.empty());

        // when
        userService.updateUser(id, userDto);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).findById(id);
        verify(userRepository).findByEmail(userDto.getEmail());

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getId()).isEqualTo(id);
        assertThat(capturedUser.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(capturedUser.getPassword()).isEqualTo(userDto.getPassword());
    }

    @Test
    void updatingUserWillThrowWhenUserNotFound() {
        // given
        Long id = 2L;
        UserDto userDto = UserDto.builder().email("test@test.com").password("test").isPasswordEncrypted(true).isAdmin(false).build();

        given(userRepository.findById(2L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.updateUser(id, userDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id")
                .hasMessageContaining(id.toString())
                .hasMessageContaining("not found");
    }

    @Test
    void updatingUserWillThrowWhenEmailIsTaken() {
        // given
        Long id = 2L;
        UserDto userDto = UserDto.builder().email("test@test.com").password("test").isPasswordEncrypted(true).isAdmin(false).build();

        given(userRepository.findById(2L)).willReturn(Optional.of(User.builder().id(id).build()));
        given(userRepository.findByEmail(userDto.getEmail())).willReturn(Optional.of(User.builder().build()));

        // when
        // then
        assertThatThrownBy(() -> userService.updateUser(id, userDto))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining("Email")
                .hasMessageContaining(userDto.getEmail())
                .hasMessageContaining("is taken");
    }

    @Test
    void canDeleteUser() {
        // given
        Long id = 2L;

        given(userRepository.findById(id)).willReturn(Optional.of(User.builder().build()));

        // when
        userService.deleteUser(id);

        // then
        verify(userRepository).deleteById(id);
    }

    @Test
    void deletingUserWillThrowWhenUserNotFound() {
        // given
        Long id = 2L;

        given(userRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.deleteUser(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id")
                .hasMessageContaining(id.toString())
                .hasMessageContaining("not found");
    }
}