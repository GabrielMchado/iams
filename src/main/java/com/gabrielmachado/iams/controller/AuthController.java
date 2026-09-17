package com.gabrielmachado.iams.controller;

import com.gabrielmachado.iams.dto.LoginRequest;
import com.gabrielmachado.iams.dto.LoginResponse;
import com.gabrielmachado.iams.dto.RegisterRequest;
import com.gabrielmachado.iams.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterRequest> register(@RequestBody RegisterRequest request) throws Exception {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<String> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok("Autenticado como: " + email);
    }

}
