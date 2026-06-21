package com.gerenciador_de_senhas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SenhaRequestDTO {

    @NotBlank(message = "O nome do serviço é obrigatório")
    @Size(max = 100, message = "O nome do serviço deve ter no máximo 100 caracteres")
    private String nomeServico;

    @Size(max = 255, message = "A URL deve ter no máximo 255 caracteres")
    private String url;

    @Size(max = 150, message = "O login do serviço deve ter no máximo 150 caracteres")
    private String loginServico;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    private String observacoes;

    private Long categoriaId;

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLoginServico() {
        return loginServico;
    }

    public void setLoginServico(String loginServico) {
        this.loginServico = loginServico;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
