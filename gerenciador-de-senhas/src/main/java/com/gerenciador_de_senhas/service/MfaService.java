package com.gerenciador_de_senhas.service;

import com.gerenciador_de_senhas.dto.MfaConfirmRequestDTO;
import com.gerenciador_de_senhas.dto.MfaDisableRequestDTO;
import com.gerenciador_de_senhas.dto.MfaSetupResponseDTO;
import com.gerenciador_de_senhas.dto.MfaStatusResponseDTO;
import com.gerenciador_de_senhas.entity.Usuario;
import com.gerenciador_de_senhas.repository.UsuarioRepository;
import com.gerenciador_de_senhas.util.CriptografiaUtil;
import com.gerenciador_de_senhas.util.TotpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MfaService {

    private final UsuarioRepository usuarioRepository;
    private final TotpUtil totpUtil;
    private final CriptografiaUtil criptografiaUtil;
    private final PasswordEncoder passwordEncoder;
    private final String issuer;

    public MfaService(UsuarioRepository usuarioRepository,
                      TotpUtil totpUtil,
                      CriptografiaUtil criptografiaUtil,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.mfa.issuer:NexoVault}") String issuer) {
        this.usuarioRepository = usuarioRepository;
        this.totpUtil = totpUtil;
        this.criptografiaUtil = criptografiaUtil;
        this.passwordEncoder = passwordEncoder;
        this.issuer = issuer;
    }

    public MfaStatusResponseDTO status(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        return new MfaStatusResponseDTO(usuario.isMfaAtivo());
    }

    public MfaSetupResponseDTO iniciarConfiguracao(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);

        if (usuario.isMfaAtivo()) {
            throw new IllegalArgumentException("MFA já está ativo para este usuário");
        }

        String secret = totpUtil.gerarSecret();
        usuario.setMfaSecretTemporario(criptografiaUtil.criptografar(secret));
        usuarioRepository.save(usuario);

        String otpauthUri = totpUtil.gerarOtpAuthUri(issuer, usuario.getEmail(), secret);
        return new MfaSetupResponseDTO(secret, otpauthUri);
    }

    public MfaStatusResponseDTO confirmarConfiguracao(Long usuarioId, MfaConfirmRequestDTO dto) {
        Usuario usuario = buscarUsuario(usuarioId);

        if (usuario.isMfaAtivo()) {
            throw new IllegalArgumentException("MFA já está ativo para este usuário");
        }

        if (usuario.getMfaSecretTemporario() == null || usuario.getMfaSecretTemporario().isBlank()) {
            throw new IllegalArgumentException("Inicie a configuração do MFA antes de confirmar");
        }

        String secret = criptografiaUtil.descriptografar(usuario.getMfaSecretTemporario());
        boolean codigoValido = totpUtil.validarCodigo(secret, dto.getCodigo());

        if (!codigoValido) {
            throw new IllegalArgumentException("Código MFA inválido");
        }

        usuario.setMfaSecret(usuario.getMfaSecretTemporario());
        usuario.setMfaSecretTemporario(null);
        usuario.setMfaAtivo(true);
        usuarioRepository.save(usuario);

        return new MfaStatusResponseDTO(true);
    }

    public MfaStatusResponseDTO desativar(Long usuarioId, MfaDisableRequestDTO dto) {
        Usuario usuario = buscarUsuario(usuarioId);

        if (!usuario.isMfaAtivo()) {
            throw new IllegalArgumentException("MFA não está ativo para este usuário");
        }

        boolean senhaCorreta = passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenhaHash());
        if (!senhaCorreta) {
            throw new IllegalArgumentException("Senha atual inválida");
        }

        String secret = criptografiaUtil.descriptografar(usuario.getMfaSecret());
        boolean codigoValido = totpUtil.validarCodigo(secret, dto.getCodigo());

        if (!codigoValido) {
            throw new IllegalArgumentException("Código MFA inválido");
        }

        usuario.setMfaAtivo(false);
        usuario.setMfaSecret(null);
        usuario.setMfaSecretTemporario(null);
        usuarioRepository.save(usuario);

        return new MfaStatusResponseDTO(false);
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }
}
