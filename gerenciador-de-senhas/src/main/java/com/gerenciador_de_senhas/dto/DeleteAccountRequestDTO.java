package com.gerenciador_de_senhas.dto;

import jakarta.validation.constraints.NotBlank;

public class DeleteAccountRequestDTO {

    @NotBlank(message = "A senha atual é obrigatória")
    private String currentPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
