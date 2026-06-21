package com.gerenciador_de_senhas.repository;

import com.gerenciador_de_senhas.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioIdOrderByNomeAsc(Long usuarioId);

    Optional<Categoria> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByNomeIgnoreCaseAndUsuarioId(String nome, Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
