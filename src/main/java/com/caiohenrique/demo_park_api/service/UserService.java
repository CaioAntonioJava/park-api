package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import com.caiohenrique.demo_park_api.exception.PasswordInvalidException;
import com.caiohenrique.demo_park_api.exception.UserNameUniqueViolationException;
import com.caiohenrique.demo_park_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("""
                        Usuário ID { %d } não encontrado.
                        """, id))
        );
    }

    @Transactional
    public User changePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {

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
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
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

    @Transactional
    public void resetPassword(Long id,
                              String newPassword,
                              String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordInvalidException(
                    "Os campos nova senha e confirmação de senha devem ser idênticos.");
        }

        User user = findById(id);

        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
