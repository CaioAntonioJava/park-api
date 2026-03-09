package com.caiohenrique.demo_park_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class SpringJpaAuditingConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        // Obtém o usuário autenticado na requisição atual.
        // O SecurityContextHolder é onde o Spring Security guarda o objeto Authentication
        // após o processo de login ou validação do JWT.
        // A partir dele conseguimos saber quem é o usuário logado para registrar
        // na auditoria das entidades (createdBy e updatedBy).
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        return Optional.of(authentication.getName());
    }
}
