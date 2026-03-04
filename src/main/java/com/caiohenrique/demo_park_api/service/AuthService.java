package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.jwt.JwtToken;
import com.caiohenrique.demo_park_api.jwt.JwtUserDetails;
import com.caiohenrique.demo_park_api.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    public JwtToken authenticate(Authentication authentication) {

        JwtUserDetails userDetails =
                (JwtUserDetails) authentication.getPrincipal();

        String role = userDetails.getRole().replace("ROLE_", "");

        return JwtUtils.createToken(
                userDetails.getUsername(),
                role
        );
    }
}