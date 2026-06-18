package com.gerenciador_de_senhas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MfaConfirmRequestDTO {

    @NotBlank(message = "O código MFA é obrigatório")
    @Pattern(regexp = "^[0-9]{6}$", message = "O código MFA deve possuir 6 dígitos")
    private String codigo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
