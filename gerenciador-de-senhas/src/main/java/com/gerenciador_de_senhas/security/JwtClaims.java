package com.gerenciador_de_senhas.security;

public class JwtClaims {

    private Long usuarioId;
    private String email;
    private String tipo;

    public JwtClaims() {
    }

    public JwtClaims(Long usuarioId, String email, String tipo) {
        this.usuarioId = usuarioId;
        this.email = email;
        this.tipo = tipo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
