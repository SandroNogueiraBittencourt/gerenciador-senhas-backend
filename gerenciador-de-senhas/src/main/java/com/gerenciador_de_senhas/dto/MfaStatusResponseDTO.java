package com.gerenciador_de_senhas.dto;

public class MfaStatusResponseDTO {

    private boolean ativo;

    public MfaStatusResponseDTO() {
    }

    public MfaStatusResponseDTO(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
