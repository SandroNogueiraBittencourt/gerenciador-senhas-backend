package com.gerenciador_de_senhas.service;

import com.gerenciador_de_senhas.dto.AuthResponseDTO;
import com.gerenciador_de_senhas.dto.LoginRequestDTO;
import com.gerenciador_de_senhas.dto.MfaVerifyRequestDTO;
import com.gerenciador_de_senhas.dto.RegisterRequestDTO;
import com.gerenciador_de_senhas.dto.UsuarioResponseDTO;
import com.gerenciador_de_senhas.entity.Usuario;
import com.gerenciador_de_senhas.mapper.UsuarioMapper;
import com.gerenciador_de_senhas.repository.UsuarioRepository;
import com.gerenciador_de_senhas.security.JwtClaims;
import com.gerenciador_de_senhas.security.JwtUtil;
import com.gerenciador_de_senhas.util.CriptografiaUtil;
import com.gerenciador_de_senhas.util.ForcaSenhaUtil;
import com.gerenciador_de_senhas.util.TotpUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final JwtUtil jwtUtil;
    private final TotpUtil totpUtil;
    private final CriptografiaUtil criptografiaUtil;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       UsuarioMapper usuarioMapper,
                       JwtUtil jwtUtil,
                       TotpUtil totpUtil,
                       CriptografiaUtil criptografiaUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.jwtUtil = jwtUtil;
        this.totpUtil = totpUtil;
        this.criptografiaUtil = criptografiaUtil;
    }

    public UsuarioResponseDTO registrar(RegisterRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        if (!ForcaSenhaUtil.senhaAtendeCriteriosMinimos(dto.getSenha())) {
            throw new IllegalArgumentException(ForcaSenhaUtil.mensagemCriteriosSenha());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setMfaAtivo(false);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha inválidos"));

        boolean senhaCorreta = passwordEncoder.matches(
                dto.getSenha(),
                usuario.getSenhaHash()
        );

        if (!senhaCorreta) {
            throw new IllegalArgumentException("E-mail ou senha inválidos");
        }

        if (usuario.isMfaAtivo()) {
            return AuthResponseDTO.requerMfa(jwtUtil.gerarMfaToken(usuario));
        }

        return gerarRespostaAutenticada(usuario);
    }

    public AuthResponseDTO verificarMfa(MfaVerifyRequestDTO dto) {
        JwtClaims claims = jwtUtil.validarMfaToken(dto.getMfaToken());

        Usuario usuario = usuarioRepository.findById(claims.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!usuario.isMfaAtivo() || usuario.getMfaSecret() == null || usuario.getMfaSecret().isBlank()) {
            throw new IllegalArgumentException("MFA não está ativo para este usuário");
        }

        String secret = criptografiaUtil.descriptografar(usuario.getMfaSecret());
        boolean codigoValido = totpUtil.validarCodigo(secret, dto.getCodigo());

        if (!codigoValido) {
            throw new IllegalArgumentException("Código MFA inválido");
        }

        return gerarRespostaAutenticada(usuario);
    }

    private AuthResponseDTO gerarRespostaAutenticada(Usuario usuario) {
        String token = jwtUtil.gerarAccessToken(usuario);
        UsuarioResponseDTO usuarioResponse = usuarioMapper.toResponseDTO(usuario);
        return AuthResponseDTO.autenticado(token, usuarioResponse);
    }
}
