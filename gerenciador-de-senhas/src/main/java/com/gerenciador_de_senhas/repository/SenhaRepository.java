package com.gerenciador_de_senhas.repository;

import com.gerenciador_de_senhas.entity.Senha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SenhaRepository extends JpaRepository<Senha, Long> {

        List<Senha> findByUsuarioIdOrderByNomeServicoAsc(Long usuarioId);

        Optional<Senha> findByIdAndUsuarioId(Long id, Long usuarioId);

        @Query(value = """
                        SELECT s.*
                        FROM senhas s
                        LEFT JOIN categorias c ON c.id = s.categoria_id
                        WHERE s.usuario_id = :usuarioId
                          AND (
                                LOWER(COALESCE(s.nome_servico, '')) LIKE :termo
                             OR LOWER(COALESCE(s.url, '')) LIKE :termo
                             OR LOWER(COALESCE(s.login_servico, '')) LIKE :termo
                             OR LOWER(COALESCE(CAST(s.observacoes AS CHAR), '')) LIKE :termo
                             OR LOWER(COALESCE(c.nome, '')) LIKE :termo
                          )
                        ORDER BY s.nome_servico ASC
                        """, nativeQuery = true)
        List<Senha> pesquisarPorTermo(
                        @Param("usuarioId") Long usuarioId,
                        @Param("termo") String termo);

        long countByUsuarioId(Long usuarioId);

        void deleteByUsuarioId(Long usuarioId);
}