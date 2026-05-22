package com.gerenciador_de_senhas.mapper;

import com.gerenciador_de_senhas.dto.CategoriaResponseDTO;
import com.gerenciador_de_senhas.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setDescricao(categoria.getDescricao());
        dto.setUsuarioId(categoria.getUsuario().getId());
        return dto;
    }
}
