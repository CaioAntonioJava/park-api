package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import com.caiohenrique.demo_park_api.exception.PasswordInvalidException;
import com.caiohenrique.demo_park_api.exception.UserNameUniqueViolationException;
import com.caiohenrique.demo_park_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test@email.com.br");
        user.setPassword("rawPassword");
        user.setRole(User.Role.ROLE_CLIENT);
    }

    @Test
    void save_withValidUser_shouldEncodePasswordAndSave() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(user);
    }

    @Test
    void save_withDuplicateUsername_shouldThrowUserNameUniqueViolationException() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserNameUniqueViolationException.class,
                () -> userService.save(user));

        verify(userRepository).save(user);
    }

    @Test
    void findById_withExistingId_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.findById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("test@email.com.br", found.getUsername());
    }

    @Test
    void findById_withNonExistingId_shouldThrowEntityNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.findById(99L));
    }

    @Test
    void changePassword_withValidData_shouldUpdatePassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", "rawPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        User updated = userService.changePassword(1L, "currentPassword", "newPassword", "newPassword");

        assertNotNull(updated);
        assertEquals("encodedNewPassword", updated.getPassword());
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void changePassword_withMismatchedConfirmPassword_shouldThrowPasswordInvalidException() {
        assertThrows(PasswordInvalidException.class,
                () -> userService.changePassword(1L, "current", "newPass", "differentPass"));
    }

    @Test
    void changePassword_withWrongCurrentPassword_shouldThrowPasswordInvalidException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "rawPassword")).thenReturn(false);

        assertThrows(PasswordInvalidException.class,
                () -> userService.changePassword(1L, "wrongPassword", "newPassword", "newPassword"));
    }

    @Test
    void findAll_shouldReturnPagedUsers() {
        Pageable pageable = Pageable.ofSize(10);
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("test@email.com.br", result.getContent().get(0).getUsername());
    }

    @Test
    void findByUserName_withExistingUsername_shouldReturnUser() {
        when(userRepository.findByUsername("test@email.com.br")).thenReturn(Optional.of(user));

        User found = userService.findByUserName("test@email.com.br");

        assertNotNull(found);
        assertEquals("test@email.com.br", found.getUsername());
    }

    @Test
    void findByUserName_withNonExistingUsername_shouldThrowEntityNotFoundException() {
        when(userRepository.findByUsername("notfound@email.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.findByUserName("notfound@email.com"));
    }

    @Test
    void findRoleByUsername_shouldReturnRole() {
        when(userRepository.findRoleByUsername("test@email.com.br")).thenReturn(User.Role.ROLE_CLIENT);

        User.Role role = userService.findRoleByUsername("test@email.com.br");

        assertEquals(User.Role.ROLE_CLIENT, role);
    }

    @Test
    void resetPassword_withValidData_shouldUpdatePassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userService.resetPassword(1L, "newPassword", "newPassword");

        assertEquals("encodedNewPassword", user.getPassword());
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void resetPassword_withMismatchedConfirmPassword_shouldThrowPasswordInvalidException() {
        assertThrows(PasswordInvalidException.class,
                () -> userService.resetPassword(1L, "newPass", "differentPass"));
    }
}
