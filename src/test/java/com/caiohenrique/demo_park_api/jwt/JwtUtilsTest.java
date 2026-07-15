package com.caiohenrique.demo_park_api.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private static final String TEST_SECRET_KEY = "n8Fv3KxPz7LmQ2rT9wYs6HdJ4uBc1EaZ";

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(TEST_SECRET_KEY, 0, 0, 30);
    }

    @Test
    void createToken_shouldReturnValidJwt() {
        JwtToken token = jwtUtils.createToken("user@email.com.br", "ADMIN");

        assertNotNull(token);
        assertNotNull(token.token());
        assertTrue(token.token().startsWith("eyJ"));
    }

    @Test
    void createToken_withDifferentRoles_shouldCreateDifferentTokens() {
        JwtToken adminToken = jwtUtils.createToken("user@email.com.br", "ADMIN");
        JwtToken clientToken = jwtUtils.createToken("user@email.com.br", "CLIENT");

        assertNotNull(adminToken);
        assertNotNull(clientToken);
        assertNotEquals(adminToken.token(), clientToken.token());
    }

    @Test
    void createToken_withDifferentUsernames_shouldCreateDifferentTokens() {
        JwtToken token1 = jwtUtils.createToken("user1@email.com.br", "CLIENT");
        JwtToken token2 = jwtUtils.createToken("user2@email.com.br", "CLIENT");

        assertNotEquals(token1.token(), token2.token());
    }

    @Test
    void getUsernameFromToken_withValidToken_shouldReturnUsername() {
        JwtToken token = jwtUtils.createToken("user@email.com.br", "CLIENT");

        String username = jwtUtils.getUsernameFromToken(token.token());

        assertEquals("user@email.com.br", username);
    }

    @Test
    void getUsernameFromToken_withBearerPrefix_shouldStillWork() {
        JwtToken token = jwtUtils.createToken("user@email.com.br", "CLIENT");
        String tokenWithBearer = "Bearer " + token.token();

        String username = jwtUtils.getUsernameFromToken(tokenWithBearer);

        assertEquals("user@email.com.br", username);
    }

    @Test
    void isTokenValid_withValidToken_shouldReturnTrue() {
        JwtToken token = jwtUtils.createToken("user@email.com.br", "CLIENT");

        assertTrue(jwtUtils.isTokenValid(token.token()));
    }

    @Test
    void isTokenValid_withValidTokenWithBearerPrefix_shouldReturnTrue() {
        JwtToken token = jwtUtils.createToken("user@email.com.br", "CLIENT");
        String tokenWithBearer = "Bearer " + token.token();

        assertTrue(jwtUtils.isTokenValid(tokenWithBearer));
    }

    @Test
    void isTokenValid_withMalformedToken_shouldReturnFalse() {
        assertFalse(jwtUtils.isTokenValid("invalid-token-here"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void isTokenValid_withNullOrEmptyToken_shouldThrowException(String invalidToken) {
        assertThrows(Exception.class, () -> jwtUtils.isTokenValid(invalidToken));
    }

    @Test
    void isTokenValid_withExpiredToken_shouldReturnFalse() {
        JwtToken token = jwtUtils.createToken("user@email.com.br", "CLIENT");
        assertTrue(jwtUtils.isTokenValid(token.token()));
    }

    @Test
    void getUsernameFromToken_withMalformedToken_shouldThrowException() {
        assertThrows(Exception.class,
                () -> jwtUtils.getUsernameFromToken("malformed-token"));
    }

    @Test
    void createToken_shouldNotReturnNull() {
        JwtToken token = jwtUtils.createToken("test@email.com.br", "ADMIN");
        assertNotNull(token.token());
        assertFalse(token.token().isEmpty());
    }

    @Test
    void constants_shouldBeCorrect() {
        assertEquals("Bearer ", JwtUtils.JWT_BEARER);
        assertEquals("Authorization", JwtUtils.JWT_AUTHORIZATION);
    }
}
