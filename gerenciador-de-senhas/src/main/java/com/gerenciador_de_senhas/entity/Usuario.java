package com.gerenciador_de_senhas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "mfa_ativo", nullable = false)
    private boolean mfaAtivo = false;

    @Column(name = "mfa_secret", columnDefinition = "TEXT")
    private String mfaSecret;

    @Column(name = "mfa_secret_temporario", columnDefinition = "TEXT")
    private String mfaSecretTemporario;

    @OneToMany(mappedBy = "usuario")
    private List<Senha> senhas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<Categoria> categorias = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nome, String email, String senhaHash) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.dataCriacao = LocalDateTime.now();
    }


    @PrePersist
    public void prePersist() {
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
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

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean isMfaAtivo() {
        return mfaAtivo;
    }

    public void setMfaAtivo(boolean mfaAtivo) {
        this.mfaAtivo = mfaAtivo;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    public String getMfaSecretTemporario() {
        return mfaSecretTemporario;
    }

    public void setMfaSecretTemporario(String mfaSecretTemporario) {
        this.mfaSecretTemporario = mfaSecretTemporario;
    }

    public List<Senha> getSenhas() {
        return senhas;
    }

    public void setSenhas(List<Senha> senhas) {
        this.senhas = senhas;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }
}
