package com.gerenciador_de_senhas.dto;

public class AuthResponseDTO {

    private String status;
    private String token;
    private String mfaToken;
    private UsuarioResponseDTO usuario;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String status, String token, String mfaToken, UsuarioResponseDTO usuario) {
        this.status = status;
        this.token = token;
        this.mfaToken = mfaToken;
        this.usuario = usuario;
    }

    public static AuthResponseDTO autenticado(String token, UsuarioResponseDTO usuario) {
        return new AuthResponseDTO("AUTHENTICATED", token, null, usuario);
    }

    public static AuthResponseDTO requerMfa(String mfaToken) {
        return new AuthResponseDTO("MFA_REQUIRED", null, mfaToken, null);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMfaToken() {
        return mfaToken;
    }

    public void setMfaToken(String mfaToken) {
        this.mfaToken = mfaToken;
    }

    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDTO usuario) {
        this.usuario = usuario;
    }
}
