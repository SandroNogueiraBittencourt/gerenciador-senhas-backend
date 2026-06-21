package com.gerenciador_de_senhas.controller;

import com.gerenciador_de_senhas.dto.AccountInfoResponseDTO;
import com.gerenciador_de_senhas.dto.ChangeEmailRequestDTO;
import com.gerenciador_de_senhas.dto.ChangePasswordRequestDTO;
import com.gerenciador_de_senhas.dto.DeleteAccountRequestDTO;
import com.gerenciador_de_senhas.dto.ExportDataResponseDTO;
import com.gerenciador_de_senhas.dto.MessageResponseDTO;
import com.gerenciador_de_senhas.security.JwtClaims;
import com.gerenciador_de_senhas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/account-info")
    public ResponseEntity<AccountInfoResponseDTO> buscarInformacoesConta(Authentication authentication) {
        Long usuarioId = obterUsuarioId(authentication);
        return ResponseEntity.ok(usuarioService.buscarInformacoesConta(usuarioId));
    }

    @PutMapping("/change-password")
    public ResponseEntity<MessageResponseDTO> alterarSenha(
            @Valid @RequestBody ChangePasswordRequestDTO dto,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        usuarioService.alterarSenha(usuarioId, dto);
        return ResponseEntity.ok(new MessageResponseDTO("Senha atualizada com sucesso."));
    }

    @PutMapping("/change-email")
    public ResponseEntity<MessageResponseDTO> alterarEmail(
            @Valid @RequestBody ChangeEmailRequestDTO dto,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        usuarioService.alterarEmail(usuarioId, dto);
        return ResponseEntity.ok(new MessageResponseDTO("E-mail atualizado com sucesso."));
    }

    @GetMapping("/export-data")
    public ResponseEntity<byte[]> exportarDados(Authentication authentication) {
        Long usuarioId = obterUsuarioId(authentication);
        ExportDataResponseDTO exportacao = usuarioService.exportarDados(usuarioId);
        byte[] conteudo = exportacao.getContent().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(exportacao.getFileName(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(conteudo);
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<MessageResponseDTO> excluirConta(
            @Valid @RequestBody DeleteAccountRequestDTO dto,
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);
        usuarioService.excluirConta(usuarioId, dto);
        return ResponseEntity.ok(new MessageResponseDTO("Conta excluída com sucesso."));
    }

    private Long obterUsuarioId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtClaims claims)) {
            throw new IllegalArgumentException("Usuário autenticado não encontrado");
        }

        return claims.getUsuarioId();
    }
}
