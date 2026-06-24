package com.caiohenrique.demo_park_api.jwt;

import com.caiohenrique.demo_park_api.web.exception.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("Http Status 401 {}", authException.getMessage());

        response.setHeader("WWW-Authenticate", "Bearer realm='/api/v1/auth/login'");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = new ObjectMapper().writeValueAsString(
                new ErrorMessage(request, HttpStatus.UNAUTHORIZED, "Token ausente ou inválido.")
        );
        response.getWriter().write(body);
    }
}
