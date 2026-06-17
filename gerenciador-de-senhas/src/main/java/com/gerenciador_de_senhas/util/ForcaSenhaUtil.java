package com.gerenciador_de_senhas.util;

public final class ForcaSenhaUtil {

    private ForcaSenhaUtil() {
    }

    public static boolean senhaAtendeCriteriosMinimos(String senha) {
        if (senha == null) {
            return false;
        }

        boolean tamanhoMinimo = senha.length() >= 8;
        boolean temMaiuscula = senha.matches(".*[A-Z].*");
        boolean temMinuscula = senha.matches(".*[a-z].*");
        boolean temNumero = senha.matches(".*[0-9].*");
        boolean temEspecial = senha.matches(".*[^A-Za-z0-9].*");

        return tamanhoMinimo
                && temMaiuscula
                && temMinuscula
                && temNumero
                && temEspecial;
    }

    public static String mensagemCriteriosSenha() {
        return "A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, "
                + "uma letra minúscula, um número e um caractere especial.";
    }

    public static String avaliar(String senha) {
        if (senha == null || senha.isBlank()) {
            return "FRACA";
        }

        boolean tamanhoMinimo = senha.length() >= 8;
        boolean temMaiuscula = senha.matches(".*[A-Z].*");
        boolean temMinuscula = senha.matches(".*[a-z].*");
        boolean temNumero = senha.matches(".*[0-9].*");
        boolean temEspecial = senha.matches(".*[^A-Za-z0-9].*");
        boolean tamanhoReforcado = senha.length() >= 12;

        int pontos = 0;

        if (tamanhoMinimo) pontos++;
        if (temMaiuscula) pontos++;
        if (temMinuscula) pontos++;
        if (temNumero) pontos++;
        if (temEspecial) pontos++;
        if (tamanhoReforcado) pontos++;

        if (pontos >= 5) {
            return "FORTE";
        }

        if (pontos >= 3) {
            return "MEDIA";
        }

        return "FRACA";
    }
}