package com.caiohenrique.demo_park_api.repository;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.repository.projection.ClientProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_shouldPersistUser() {
        User user = new User();
        user.setUsername("test@email.com.br");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.ROLE_CLIENT);

        User saved = userRepository.save(user);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("test@email.com.br", saved.getUsername());
    }

    @Test
    void findByUsername_withExistingUser_shouldReturnUser() {
        User user = new User();
        user.setUsername("find@email.com.br");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.ROLE_ADMIN);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("find@email.com.br");

        assertTrue(found.isPresent());
        assertEquals("find@email.com.br", found.get().getUsername());
        assertEquals(User.Role.ROLE_ADMIN, found.get().getRole());
    }

    @Test
    void findByUsername_withNonExistingUser_shouldReturnEmpty() {
        Optional<User> found = userRepository.findByUsername("nonexistent@email.com");

        assertFalse(found.isPresent());
    }

    @Test
    void findRoleByUsername_shouldReturnRole() {
        User user = new User();
        user.setUsername("role@email.com.br");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.ROLE_CLIENT);
        userRepository.save(user);

        User.Role role = userRepository.findRoleByUsername("role@email.com.br");

        assertEquals(User.Role.ROLE_CLIENT, role);
    }

    @Test
    void save_withDuplicateUsername_shouldThrowException() {
        User user1 = new User();
        user1.setUsername("duplicate@email.com.br");
        user1.setPassword("pass1");
        user1.setRole(User.Role.ROLE_CLIENT);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("duplicate@email.com.br");
        user2.setPassword("pass2");
        user2.setRole(User.Role.ROLE_ADMIN);

        assertThrows(Exception.class, () -> userRepository.save(user2));
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        User user1 = new User();
        user1.setUsername("user1@email.com.br");
        user1.setPassword("pass1");
        user1.setRole(User.Role.ROLE_CLIENT);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2@email.com.br");
        user2.setPassword("pass2");
        user2.setRole(User.Role.ROLE_ADMIN);
        userRepository.save(user2);

        Page<User> users = userRepository.findAll(PageRequest.of(0, 10));

        assertEquals(2, users.getTotalElements());
    }
}
