package com.gerenciador_de_senhas.dto;

public class MfaSetupResponseDTO {

    private String secret;
    private String otpauthUri;

    public MfaSetupResponseDTO() {
    }

    public MfaSetupResponseDTO(String secret, String otpauthUri) {
        this.secret = secret;
        this.otpauthUri = otpauthUri;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getOtpauthUri() {
        return otpauthUri;
    }

    public void setOtpauthUri(String otpauthUri) {
        this.otpauthUri = otpauthUri;
    }
}
