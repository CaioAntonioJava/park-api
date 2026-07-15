package com.caiohenrique.demo_park_api.jwt;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user@email.com.br");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.ROLE_CLIENT);
    }

    @Test
    void loadUserByUsername_withExistingUser_shouldReturnJwtUserDetails() {
        when(userService.findByUserName("user@email.com.br")).thenReturn(user);

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("user@email.com.br");

        assertNotNull(userDetails);
        assertInstanceOf(JwtUserDetails.class, userDetails);
        assertEquals("user@email.com.br", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT")));
    }

    @Test
    void loadUserByUsername_withNonExistingUser_shouldThrowUsernameNotFoundException() {
        when(userService.findByUserName("notfound@email.com"))
                .thenThrow(new com.caiohenrique.demo_park_api.exception.EntityNotFoundException("User not found"));

        assertThrows(com.caiohenrique.demo_park_api.exception.EntityNotFoundException.class,
                () -> jwtUserDetailsService.loadUserByUsername("notfound@email.com"));
    }

    @Test
    void loadUserByUsername_withAdminUser_shouldReturnAdminAuthority() {
        user.setRole(User.Role.ROLE_ADMIN);
        when(userService.findByUserName("admin@email.com.br")).thenReturn(user);

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("admin@email.com.br");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
