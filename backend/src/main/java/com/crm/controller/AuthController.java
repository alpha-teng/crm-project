package com.crm.controller;

import com.crm.dto.request.LoginRequest;
import com.crm.dto.response.LoginResponse;
import com.crm.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final IUserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest request) {
        log.info("用户注册: {}", request.getUsername());
        userService.register(request, request.getUsername(), "user");
        return ResponseEntity.status(HttpStatus.CREATED).body("注册成功");
    }
    
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser(@RequestAttribute Integer userId) {
        LoginResponse response = userService.getUserInfo(userId);
        return ResponseEntity.ok(response);
    }
}
