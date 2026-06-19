package com.gerenciador_de_senhas.controller;

import com.gerenciador_de_senhas.dto.SenhaRequestDTO;
import com.gerenciador_de_senhas.dto.SenhaResponseDTO;
import com.gerenciador_de_senhas.security.JwtClaims;
import com.gerenciador_de_senhas.service.SenhaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/passwords")
public class SenhaController {

    private final SenhaService senhaService;

    public SenhaController(SenhaService senhaService) {
        this.senhaService = senhaService;
    }

    @PostMapping
    public ResponseEntity<SenhaResponseDTO> cadastrar(
            @Valid @RequestBody SenhaRequestDTO dto,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        SenhaResponseDTO senha = senhaService.cadastrar(dto, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(senha);
    }

    @GetMapping
    public ResponseEntity<List<SenhaResponseDTO>> listar(Authentication authentication) {
        Long usuarioId = obterUsuarioId(authentication);
        return ResponseEntity.ok(senhaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SenhaResponseDTO> buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        return ResponseEntity.ok(senhaService.buscarPorId(id, usuarioId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SenhaResponseDTO>> pesquisar(
            @RequestParam String termo,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        return ResponseEntity.ok(senhaService.pesquisar(usuarioId, termo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SenhaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody SenhaRequestDTO dto,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        return ResponseEntity.ok(senhaService.atualizar(id, dto, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        senhaService.excluir(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    private Long obterUsuarioId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtClaims claims)) {
            throw new IllegalArgumentException("Usuário autenticado não encontrado");
        }

        return claims.getUsuarioId();
    }
}
