package com.gerenciador_de_senhas.service;

import com.gerenciador_de_senhas.dto.CategoriaRequestDTO;
import com.gerenciador_de_senhas.dto.CategoriaResponseDTO;
import com.gerenciador_de_senhas.entity.Categoria;
import com.gerenciador_de_senhas.entity.Usuario;
import com.gerenciador_de_senhas.mapper.CategoriaMapper;
import com.gerenciador_de_senhas.repository.CategoriaRepository;
import com.gerenciador_de_senhas.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            UsuarioRepository usuarioRepository,
                            CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public CategoriaResponseDTO cadastrar(CategoriaRequestDTO dto, Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);

        if (categoriaRepository.existsByNomeIgnoreCaseAndUsuarioId(dto.getNome(), usuarioId)) {
            throw new IllegalArgumentException("Categoria já cadastrada para este usuário");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        categoria.setUsuario(usuario);

        return categoriaMapper.toResponseDTO(categoriaRepository.save(categoria));
    }

    public List<CategoriaResponseDTO> listarPorUsuario(Long usuarioId) {
        return categoriaRepository.findByUsuarioIdOrderByNomeAsc(usuarioId)
                .stream()
                .map(categoriaMapper::toResponseDTO)
                .toList();
    }

    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto, Long usuarioId) {
        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());

        return categoriaMapper.toResponseDTO(categoriaRepository.save(categoria));
    }

    public void excluir(Long id, Long usuarioId) {
        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        categoriaRepository.delete(categoria);
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }
}
