package com.gerenciador_de_senhas.util;

public final class ForcaSenhaUtil {

    private ForcaSenhaUtil() {
    }

    public static String avaliar(String senha) {
        if (senha == null || senha.length() < 8) {
            return "FRACA";
        }

        boolean temMaiuscula = senha.matches(".*[A-Z].*");
        boolean temMinuscula = senha.matches(".*[a-z].*");
        boolean temNumero = senha.matches(".*[0-9].*");
        boolean temEspecial = senha.matches(".*[^A-Za-z0-9].*");

        int pontos = 0;
        if (temMaiuscula) pontos++;
        if (temMinuscula) pontos++;
        if (temNumero) pontos++;
        if (temEspecial) pontos++;
        if (senha.length() >= 12) pontos++;

        if (pontos >= 4) {
            return "FORTE";
        }

        if (pontos >= 3) {
            return "MEDIA";
        }

        return "FRACA";
    }
}
