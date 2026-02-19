package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.service.UserService;
import com.caiohenrique.demo_park_api.web.dto.UserCreateDto;
import com.caiohenrique.demo_park_api.web.dto.UserChangePasswordDTO;
import com.caiohenrique.demo_park_api.web.dto.UserResponseDto;
import com.caiohenrique.demo_park_api.web.dto.mapper.UserMapper;
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
    public ResponseEntity<UserResponseDto> create (@RequestBody UserCreateDto createDto) {
        User user = userService.save(UserMapper.toUser(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponseDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById (@PathVariable Long id) {
       User user = userService.findById(id);
        return ResponseEntity.ok().body(UserMapper.toResponseDto(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll () {
        List<User> users = userService.findAll();
        return ResponseEntity.ok().body(users);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updatePassword (@PathVariable Long id, @RequestBody UserChangePasswordDTO userChangePasswordDTO) {

        User user =  userService.updatePassword(id, userChangePasswordDTO.getCurrentPassword(), userChangePasswordDTO.getNewPassword(), userChangePasswordDTO.getConfirmPassword());

        return ResponseEntity.ok(UserMapper.toResponseDto(user));
    }
}
