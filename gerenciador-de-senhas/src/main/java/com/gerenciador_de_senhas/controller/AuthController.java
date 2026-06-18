package com.gerenciador_de_senhas.controller;

import com.gerenciador_de_senhas.dto.AuthResponseDTO;
import com.gerenciador_de_senhas.dto.LoginRequestDTO;
import com.gerenciador_de_senhas.dto.MfaVerifyRequestDTO;
import com.gerenciador_de_senhas.dto.RegisterRequestDTO;
import com.gerenciador_de_senhas.dto.UsuarioResponseDTO;
import com.gerenciador_de_senhas.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<AuthResponseDTO> verificarMfa(@Valid @RequestBody MfaVerifyRequestDTO dto) {
        return ResponseEntity.ok(authService.verificarMfa(dto));
    }
}
