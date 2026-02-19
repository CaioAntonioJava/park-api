package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.service.UserService;
import com.caiohenrique.demo_park_api.web.dto.UserCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.UserChangePasswordDTO;
import com.caiohenrique.demo_park_api.web.dto.UserResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping
    public ResponseEntity<UserResponseDTO> create (@Valid @RequestBody UserCreateDTO createDto) {
        User user = userService.save(UserMapper.toUser(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponseDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById (@PathVariable Long id) {
       User user = userService.findById(id);
        return ResponseEntity.ok().body(UserMapper.toResponseDto(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll () {
        List<User> users = userService.findAll();
        return ResponseEntity.ok().body(UserMapper.toListDto(users));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword (@PathVariable Long id, @Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO) {
        User user =  userService.updatePassword(
                id, userChangePasswordDTO.getCurrentPassword(), userChangePasswordDTO.getNewPassword(), userChangePasswordDTO.getConfirmPassword()
        );
        return ResponseEntity.noContent().build();
    }
}
