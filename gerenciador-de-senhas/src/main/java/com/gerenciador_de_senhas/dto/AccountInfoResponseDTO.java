package com.gerenciador_de_senhas.dto;

import java.time.LocalDateTime;

public class AccountInfoResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private LocalDateTime dataCriacao;
    private Long quantidadeSenhas;

    public AccountInfoResponseDTO() {
    }

    public AccountInfoResponseDTO(Long id, String nome, String email, LocalDateTime dataCriacao, Long quantidadeSenhas) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataCriacao = dataCriacao;
        this.quantidadeSenhas = quantidadeSenhas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Long getQuantidadeSenhas() {
        return quantidadeSenhas;
    }

    public void setQuantidadeSenhas(Long quantidadeSenhas) {
        this.quantidadeSenhas = quantidadeSenhas;
    }
}
