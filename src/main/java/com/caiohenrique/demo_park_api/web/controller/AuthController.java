package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.jwt.JwtToken;
import com.caiohenrique.demo_park_api.service.AuthService;
import com.caiohenrique.demo_park_api.web.dto.UserLoginDto;
import com.caiohenrique.demo_park_api.web.exception.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDto dto, HttpServletRequest request) {

        try {

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

            JwtToken token = authService.authenticate(authentication);

            return ResponseEntity.ok(token);

        } catch (AuthenticationException ex) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage(request, HttpStatus.UNAUTHORIZED, "Credenciais inválidas."));
        }
    }
}
