package com.gerenciador_de_senhas.service;

import com.gerenciador_de_senhas.dto.SenhaRequestDTO;
import com.gerenciador_de_senhas.dto.SenhaResponseDTO;
import com.gerenciador_de_senhas.entity.Categoria;
import com.gerenciador_de_senhas.entity.Senha;
import com.gerenciador_de_senhas.entity.Usuario;
import com.gerenciador_de_senhas.mapper.SenhaMapper;
import com.gerenciador_de_senhas.repository.CategoriaRepository;
import com.gerenciador_de_senhas.repository.SenhaRepository;
import com.gerenciador_de_senhas.repository.UsuarioRepository;
import com.gerenciador_de_senhas.util.CriptografiaUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SenhaService {

    private final SenhaRepository senhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final SenhaMapper senhaMapper;
    private final CriptografiaUtil criptografiaUtil;

    public SenhaService(SenhaRepository senhaRepository,
                        UsuarioRepository usuarioRepository,
                        CategoriaRepository categoriaRepository,
                        SenhaMapper senhaMapper,
                        CriptografiaUtil criptografiaUtil) {
        this.senhaRepository = senhaRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.senhaMapper = senhaMapper;
        this.criptografiaUtil = criptografiaUtil;
    }

    public SenhaResponseDTO cadastrar(SenhaRequestDTO dto, Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Categoria categoria = buscarCategoriaOpcional(dto.getCategoriaId(), usuarioId);

        Senha senha = new Senha();
        senha.setNomeServico(dto.getNomeServico());
        senha.setUrl(dto.getUrl());
        senha.setLoginServico(dto.getLoginServico());
        senha.setSenhaCriptografada(criptografiaUtil.criptografar(dto.getSenha()));
        senha.setObservacoes(dto.getObservacoes());
        senha.setDataCriacao(LocalDateTime.now());
        senha.setUsuario(usuario);
        senha.setCategoria(categoria);

        Senha senhaSalva = senhaRepository.save(senha);
        return senhaMapper.toResponseDTO(senhaSalva, dto.getSenha());
    }

    public List<SenhaResponseDTO> listarPorUsuario(Long usuarioId) {
        return senhaRepository.findByUsuarioIdOrderByNomeServicoAsc(usuarioId)
                .stream()
                .map(senha -> senhaMapper.toResponseDTO(
                        senha,
                        criptografiaUtil.descriptografar(senha.getSenhaCriptografada())
                ))
                .toList();
    }

    public SenhaResponseDTO buscarPorId(Long id, Long usuarioId) {
        Senha senha = senhaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Senha não encontrada"));

        return senhaMapper.toResponseDTO(
                senha,
                criptografiaUtil.descriptografar(senha.getSenhaCriptografada())
        );
    }

    public List<SenhaResponseDTO> pesquisar(Long usuarioId, String termo) {
        return senhaRepository.findByUsuarioIdAndNomeServicoContainingIgnoreCaseOrderByNomeServicoAsc(usuarioId, termo)
                .stream()
                .map(senha -> senhaMapper.toResponseDTO(
                        senha,
                        criptografiaUtil.descriptografar(senha.getSenhaCriptografada())
                ))
                .toList();
    }

    public SenhaResponseDTO atualizar(Long id, SenhaRequestDTO dto, Long usuarioId) {
        Senha senha = senhaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Senha não encontrada"));

        Categoria categoria = buscarCategoriaOpcional(dto.getCategoriaId(), usuarioId);

        senha.setNomeServico(dto.getNomeServico());
        senha.setUrl(dto.getUrl());
        senha.setLoginServico(dto.getLoginServico());
        senha.setSenhaCriptografada(criptografiaUtil.criptografar(dto.getSenha()));
        senha.setObservacoes(dto.getObservacoes());
        senha.setCategoria(categoria);
        senha.setDataAtualizacao(LocalDateTime.now());

        Senha senhaAtualizada = senhaRepository.save(senha);
        return senhaMapper.toResponseDTO(senhaAtualizada, dto.getSenha());
    }

    public void excluir(Long id, Long usuarioId) {
        Senha senha = senhaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Senha não encontrada"));

        senhaRepository.delete(senha);
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    private Categoria buscarCategoriaOpcional(Long categoriaId, Long usuarioId) {
        if (categoriaId == null) {
            return null;
        }

        return categoriaRepository.findByIdAndUsuarioId(categoriaId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
    }
}
