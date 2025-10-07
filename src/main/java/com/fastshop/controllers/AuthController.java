package com.fastshop.controllers;

import com.fastshop.dto.AuthRequestDTO;
import com.fastshop.dto.AuthResponseDTO;
import com.fastshop.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        var response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}