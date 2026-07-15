package com.caiohenrique.demo_park_api.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

    @Mock
    private JwtUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthorizationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthorizationFilter(userDetailsService);
    }

    @Test
    void doFilterInternal_withNoAuthHeader_shouldContinueChain() throws Exception {
        when(request.getHeader(JwtUtils.JWT_AUTHORIZATION)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternal_withEmptyAuthHeader_shouldContinueChain() throws Exception {
        when(request.getHeader(JwtUtils.JWT_AUTHORIZATION)).thenReturn("");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withNonBearerAuthHeader_shouldContinueChain() throws Exception {
        when(request.getHeader(JwtUtils.JWT_AUTHORIZATION)).thenReturn("Basic someToken");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withInvalidToken_shouldContinueChain() throws Exception {
        String invalidToken = "Bearer invalidTokenHere";
        when(request.getHeader(JwtUtils.JWT_AUTHORIZATION)).thenReturn(invalidToken);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternal_withValidToken_shouldAuthenticate() throws Exception {
        JwtToken validJwt = JwtUtils.createToken("user@email.com.br", "CLIENT");
        String tokenWithBearer = "Bearer " + validJwt.token();

        when(request.getHeader(JwtUtils.JWT_AUTHORIZATION)).thenReturn(tokenWithBearer);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user@email.com.br")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService).loadUserByUsername("user@email.com.br");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withValidAdminToken_shouldAuthenticate() throws Exception {
        JwtToken validJwt = JwtUtils.createToken("admin@email.com.br", "ADMIN");
        String tokenWithBearer = "Bearer " + validJwt.token();

        when(request.getHeader(JwtUtils.JWT_AUTHORIZATION)).thenReturn(tokenWithBearer);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("admin@email.com.br")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService).loadUserByUsername("admin@email.com.br");
        verify(filterChain).doFilter(request, response);
    }
}
