package com.gerenciador_de_senhas.controller;

import com.gerenciador_de_senhas.dto.MfaConfirmRequestDTO;
import com.gerenciador_de_senhas.dto.MfaDisableRequestDTO;
import com.gerenciador_de_senhas.dto.MfaSetupResponseDTO;
import com.gerenciador_de_senhas.dto.MfaStatusResponseDTO;
import com.gerenciador_de_senhas.security.JwtClaims;
import com.gerenciador_de_senhas.service.MfaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mfa")
@CrossOrigin(origins = "http://localhost:5173")
public class MfaController {

    private final MfaService mfaService;

    public MfaController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @GetMapping("/status")
    public ResponseEntity<MfaStatusResponseDTO> status(Authentication authentication) {
        return ResponseEntity.ok(mfaService.status(obterUsuarioId(authentication)));
    }

    @PostMapping("/setup")
    public ResponseEntity<MfaSetupResponseDTO> setup(Authentication authentication) {
        return ResponseEntity.ok(mfaService.iniciarConfiguracao(obterUsuarioId(authentication)));
    }

    @PostMapping("/confirm")
    public ResponseEntity<MfaStatusResponseDTO> confirmar(Authentication authentication,
                                                          @Valid @RequestBody MfaConfirmRequestDTO dto) {
        return ResponseEntity.ok(mfaService.confirmarConfiguracao(obterUsuarioId(authentication), dto));
    }

    @PostMapping("/disable")
    public ResponseEntity<MfaStatusResponseDTO> desativar(Authentication authentication,
                                                         @Valid @RequestBody MfaDisableRequestDTO dto) {
        return ResponseEntity.ok(mfaService.desativar(obterUsuarioId(authentication), dto));
    }

    private Long obterUsuarioId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtClaims claims)) {
            throw new IllegalArgumentException("Usuário autenticado não encontrado");
        }

        return claims.getUsuarioId();
    }
}
