package com.gerenciador_de_senhas.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class CriptografiaUtil {

    public String criptografar(String valor) {
        if (valor == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(valor.getBytes(StandardCharsets.UTF_8));
    }

    public String descriptografar(String valorCriptografado) {
        if (valorCriptografado == null) {
            return null;
        }
        byte[] decoded = Base64.getDecoder().decode(valorCriptografado);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
