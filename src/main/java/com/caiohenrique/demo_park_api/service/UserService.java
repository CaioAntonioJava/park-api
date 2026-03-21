package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import com.caiohenrique.demo_park_api.exception.PasswordInvalidException;
import com.caiohenrique.demo_park_api.exception.UserNameUniqueViolationException;
import com.caiohenrique.demo_park_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User save(User user) {

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new UserNameUniqueViolationException(String.format("""
                    Username { %s } já cadastrado
                    """, user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("""
                        Usuário ID { %d } não encontrado.
                        """, id))
        );
        return user;
    }

    @Transactional
    public User updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordInvalidException("Os campos nova senha e confirmação de senha devem ser idênticos.");
        }

        User user = findById(id);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordInvalidException("A senha atual é inválida.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }


// ===== Methods required by Spring Security =====

    @Transactional(readOnly = true)
    public User findByUserName(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("""
                        Usuário com username: { %s } não encontrado.
                        """, username)));
    }

    @Transactional(readOnly = true)
    public User.Role findRoleByUsername(String username) {
        return userRepository.findRoleByUsername(username);
    }
}
