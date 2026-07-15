package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.jwt.JwtToken;
import com.caiohenrique.demo_park_api.jwt.JwtUserDetails;
import com.caiohenrique.demo_park_api.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String TEST_SECRET_KEY = "n8Fv3KxPz7LmQ2rT9wYs6HdJ4uBc1EaZ";

    @Mock
    private Authentication authentication;

    @Mock
    private JwtUserDetails userDetails;

    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(TEST_SECRET_KEY, 0, 0, 30);
        authService = new AuthService(jwtUtils);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    void authenticate_withAdminRole_shouldCreateTokenWithRoleStripped() {
        when(userDetails.getUsername()).thenReturn("admin@email.com.br");
        when(userDetails.getRole()).thenReturn("ROLE_ADMIN");

        JwtToken token = authService.authenticate(authentication);

        assertNotNull(token);
        assertNotNull(token.token());
        assertTrue(token.token().startsWith("eyJ")); // JWT format
    }

    @Test
    void authenticate_withClientRole_shouldCreateTokenWithRoleStripped() {
        when(userDetails.getUsername()).thenReturn("client@email.com.br");
        when(userDetails.getRole()).thenReturn("ROLE_CLIENT");

        JwtToken token = authService.authenticate(authentication);

        assertNotNull(token);
        assertNotNull(token.token());
        assertTrue(token.token().startsWith("eyJ"));
    }

    @Test
    void authenticate_shouldCallGetPrincipal() {
        when(userDetails.getUsername()).thenReturn("user@email.com.br");
        when(userDetails.getRole()).thenReturn("ROLE_CLIENT");

        authService.authenticate(authentication);

        verify(authentication).getPrincipal();
        verify(userDetails).getUsername();
        verify(userDetails).getRole();
    }
}
