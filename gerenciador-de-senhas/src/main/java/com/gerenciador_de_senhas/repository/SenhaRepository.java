package com.gerenciador_de_senhas.repository;

import com.gerenciador_de_senhas.entity.Senha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SenhaRepository extends JpaRepository<Senha, Long> {

    List<Senha> findByUsuarioIdOrderByNomeServicoAsc(Long usuarioId);

    Optional<Senha> findByIdAndUsuarioId(Long id, Long usuarioId);

    List<Senha> findByUsuarioIdAndNomeServicoContainingIgnoreCaseOrderByNomeServicoAsc(Long usuarioId, String nomeServico);

    long countByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
