package com.gerenciador_de_senhas.mapper;

import com.gerenciador_de_senhas.dto.SenhaResponseDTO;
import com.gerenciador_de_senhas.entity.Senha;
import org.springframework.stereotype.Component;

@Component
public class SenhaMapper {

    public SenhaResponseDTO toResponseDTO(Senha senha, String senhaDescriptografada) {
        SenhaResponseDTO dto = new SenhaResponseDTO();
        dto.setId(senha.getId());
        dto.setNomeServico(senha.getNomeServico());
        dto.setUrl(senha.getUrl());
        dto.setLoginServico(senha.getLoginServico());
        dto.setSenha(senhaDescriptografada);
        dto.setObservacoes(senha.getObservacoes());
        dto.setDataCriacao(senha.getDataCriacao());
        dto.setDataAtualizacao(senha.getDataAtualizacao());
        dto.setUsuarioId(senha.getUsuario().getId());

        if (senha.getCategoria() != null) {
            dto.setCategoriaId(senha.getCategoria().getId());
            dto.setCategoriaNome(senha.getCategoria().getNome());
        }

        return dto;
    }
}
