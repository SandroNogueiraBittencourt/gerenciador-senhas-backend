package com.gerenciador_de_senhas.service;

import com.gerenciador_de_senhas.dto.LoginRequestDTO;
import com.gerenciador_de_senhas.dto.RegisterRequestDTO;
import com.gerenciador_de_senhas.dto.UsuarioResponseDTO;
import com.gerenciador_de_senhas.entity.Usuario;
import com.gerenciador_de_senhas.mapper.UsuarioMapper;
import com.gerenciador_de_senhas.repository.UsuarioRepository;
import com.gerenciador_de_senhas.util.ForcaSenhaUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
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

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    public UsuarioResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha inválidos"));

        boolean senhaCorreta = passwordEncoder.matches(
                dto.getSenha(),
                usuario.getSenhaHash()
        );

        if (!senhaCorreta) {
            throw new IllegalArgumentException("E-mail ou senha inválidos");
        }

        return usuarioMapper.toResponseDTO(usuario);
    }
}