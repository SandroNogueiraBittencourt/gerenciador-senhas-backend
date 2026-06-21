package com.gerenciador_de_senhas.service;

import com.gerenciador_de_senhas.dto.AccountInfoResponseDTO;
import com.gerenciador_de_senhas.dto.ChangeEmailRequestDTO;
import com.gerenciador_de_senhas.dto.ChangePasswordRequestDTO;
import com.gerenciador_de_senhas.dto.DeleteAccountRequestDTO;
import com.gerenciador_de_senhas.dto.ExportDataResponseDTO;
import com.gerenciador_de_senhas.entity.Senha;
import com.gerenciador_de_senhas.entity.Usuario;
import com.gerenciador_de_senhas.repository.CategoriaRepository;
import com.gerenciador_de_senhas.repository.SenhaRepository;
import com.gerenciador_de_senhas.repository.UsuarioRepository;
import com.gerenciador_de_senhas.util.CriptografiaUtil;
import com.gerenciador_de_senhas.util.ForcaSenhaUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {

    private static final DateTimeFormatter DATA_ARQUIVO = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final UsuarioRepository usuarioRepository;
    private final SenhaRepository senhaRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final CriptografiaUtil criptografiaUtil;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          SenhaRepository senhaRepository,
                          CategoriaRepository categoriaRepository,
                          PasswordEncoder passwordEncoder,
                          CriptografiaUtil criptografiaUtil) {
        this.usuarioRepository = usuarioRepository;
        this.senhaRepository = senhaRepository;
        this.categoriaRepository = categoriaRepository;
        this.passwordEncoder = passwordEncoder;
        this.criptografiaUtil = criptografiaUtil;
    }

    @Transactional(readOnly = true)
    public AccountInfoResponseDTO buscarInformacoesConta(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        long quantidadeSenhas = senhaRepository.countByUsuarioId(usuarioId);

        return new AccountInfoResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCriacao(),
                quantidadeSenhas
        );
    }

    @Transactional
    public void alterarSenha(Long usuarioId, ChangePasswordRequestDTO dto) {
        Usuario usuario = buscarUsuario(usuarioId);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), usuario.getSenhaHash())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        if (!ForcaSenhaUtil.senhaAtendeCriteriosMinimos(dto.getNewPassword())) {
            throw new IllegalArgumentException(ForcaSenhaUtil.mensagemCriteriosSenha());
        }

        usuario.setSenhaHash(passwordEncoder.encode(dto.getNewPassword()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarEmail(Long usuarioId, ChangeEmailRequestDTO dto) {
        Usuario usuario = buscarUsuario(usuarioId);
        String novoEmail = normalizarEmail(dto.getNewEmail());

        if (!usuario.getEmail().equalsIgnoreCase(novoEmail) && usuarioRepository.existsByEmail(novoEmail)) {
            throw new IllegalArgumentException("Este e-mail já está em uso");
        }

        usuario.setEmail(novoEmail);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public ExportDataResponseDTO exportarDados(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        List<Senha> senhas = senhaRepository.findByUsuarioIdOrderByNomeServicoAsc(usuarioId);
        LocalDateTime agora = LocalDateTime.now();

        StringBuilder conteudo = new StringBuilder();
        conteudo.append("NexoVault - Exportação de Dados\n\n");
        conteudo.append("Usuário: ").append(valorSeguro(usuario.getNome())).append("\n");
        conteudo.append("E-mail: ").append(valorSeguro(usuario.getEmail())).append("\n");
        conteudo.append("Data da exportação: ").append(DATA_BR.format(agora)).append("\n\n");

        if (senhas.isEmpty()) {
            conteudo.append("Nenhuma senha cadastrada.\n");
        }

        for (Senha senha : senhas) {
            conteudo.append("---\n\n");
            conteudo.append("Senha:\n");
            conteudo.append("Nome: ").append(valorSeguro(senha.getNomeServico())).append("\n");
            conteudo.append("URL: ").append(valorSeguro(senha.getUrl())).append("\n");
            conteudo.append("Login: ").append(valorSeguro(senha.getLoginServico())).append("\n");
            conteudo.append("Senha: ").append(valorSeguro(criptografiaUtil.descriptografar(senha.getSenhaCriptografada()))).append("\n");
            conteudo.append("Categoria: ").append(senha.getCategoria() != null ? valorSeguro(senha.getCategoria().getNome()) : "Sem categoria").append("\n");
            conteudo.append("Observações: ").append(valorSeguro(senha.getObservacoes())).append("\n\n");
        }

        String nomeArquivo = "NexoVault-"
                + sanitizarNomeArquivo(usuario.getEmail())
                + "-"
                + DATA_ARQUIVO.format(agora)
                + ".txt";

        return new ExportDataResponseDTO(nomeArquivo, conteudo.toString());
    }

    @Transactional
    public void excluirConta(Long usuarioId, DeleteAccountRequestDTO dto) {
        Usuario usuario = buscarUsuario(usuarioId);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), usuario.getSenhaHash())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        senhaRepository.deleteByUsuarioId(usuarioId);
        categoriaRepository.deleteByUsuarioId(usuarioId);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String valorSeguro(String valor) {
        if (valor == null || valor.isBlank()) {
            return "-";
        }

        return valor;
    }

    private String sanitizarNomeArquivo(String valor) {
        if (valor == null || valor.isBlank()) {
            return "usuario";
        }

        return valor.trim().replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
