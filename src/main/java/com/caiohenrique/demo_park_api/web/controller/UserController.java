package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping
    public ResponseEntity<User> create (@RequestBody User user) {
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById (@PathVariable Long id) {
       User user = userService.findById(id);
        return ResponseEntity.ok().body(user);
    }
}
