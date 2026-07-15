package com.caiohenrique.demo_park_api.jwt;

import com.caiohenrique.demo_park_api.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUserDetailsTest {

    @Test
    void constructor_shouldSetUsernamePasswordAndAuthorities() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user@email.com.br");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.ROLE_CLIENT);

        JwtUserDetails jwtUserDetails = new JwtUserDetails(user);

        assertEquals("user@email.com.br", jwtUserDetails.getUsername());
        assertEquals("encodedPassword", jwtUserDetails.getPassword());
        assertTrue(jwtUserDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT")));
    }

    @Test
    void getId_shouldReturnUserId() {
        User user = new User();
        user.setId(42L);
        user.setUsername("user@email.com.br");
        user.setPassword("pass");
        user.setRole(User.Role.ROLE_ADMIN);

        JwtUserDetails jwtUserDetails = new JwtUserDetails(user);

        assertEquals(42L, jwtUserDetails.getId());
    }

    @Test
    void getRole_shouldReturnRoleName() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user@email.com.br");
        user.setPassword("pass");
        user.setRole(User.Role.ROLE_ADMIN);

        JwtUserDetails jwtUserDetails = new JwtUserDetails(user);

        assertEquals("ROLE_ADMIN", jwtUserDetails.getRole());
    }

    @Test
    void getRole_withClientRole_shouldReturnRoleName() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user@email.com.br");
        user.setPassword("pass");
        user.setRole(User.Role.ROLE_CLIENT);

        JwtUserDetails jwtUserDetails = new JwtUserDetails(user);

        assertEquals("ROLE_CLIENT", jwtUserDetails.getRole());
    }
}
