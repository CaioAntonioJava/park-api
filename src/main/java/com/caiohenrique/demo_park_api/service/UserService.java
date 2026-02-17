package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService  {
    private final UserRepository userRepository;

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @ReadOnlyProperty
    public User findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException(String.format("""
                    Usuário ID: %d não encontrado.    
                        """, id))
        );
        return user;
    }
}
