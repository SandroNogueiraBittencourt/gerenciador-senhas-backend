package com.gerenciador_de_senhas.controller;

import com.gerenciador_de_senhas.dto.CategoriaRequestDTO;
import com.gerenciador_de_senhas.dto.CategoriaResponseDTO;
import com.gerenciador_de_senhas.security.JwtClaims;
import com.gerenciador_de_senhas.service.CategoriaService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> cadastrar(
            @Valid @RequestBody CategoriaRequestDTO dto,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        CategoriaResponseDTO categoria = categoriaService.cadastrar(dto, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listar(Authentication authentication) {
        Long usuarioId = obterUsuarioId(authentication);
        return ResponseEntity.ok(categoriaService.listarPorUsuario(usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO dto,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        return ResponseEntity.ok(categoriaService.atualizar(id, dto, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        categoriaService.excluir(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    private Long obterUsuarioId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtClaims claims)) {
            throw new IllegalArgumentException("Usuário autenticado não encontrado");
        }

        return claims.getUsuarioId();
    }
}
