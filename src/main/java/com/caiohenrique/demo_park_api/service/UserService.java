package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Transactional
    public User updatePassword (Long id, String currentPassword, String newPassword, String confirmPassword  ) {

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Os campos nova senha e confirmação de senha devem ser idênticos.");
        }

        User user = findById(id);

        if (!user.getPassword().equals(currentPassword)) {
            throw new RuntimeException("A senha atual é inválida.");
        }

        user.setPassword(newPassword);

        return user;
    }

    @ReadOnlyProperty
    public List<User> findAll () {
        return userRepository.findAll();
    }
}
