package com.gerenciador_de_senhas.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class CriptografiaUtil {

    private static final String ALGORITMO_CHAVE = "AES";
    private static final String TRANSFORMACAO = "AES/GCM/NoPadding";
    private static final String PREFIXO_AES_GCM = "v1:";
    private static final int TAMANHO_IV_BYTES = 12;
    private static final int TAMANHO_TAG_BITS = 128;

    private final SecureRandom secureRandom = new SecureRandom();
    private final SecretKeySpec chaveSecreta;

    public CriptografiaUtil(@Value("${app.crypto.key:}") String chaveConfigurada) {
        byte[] chave = carregarChave(chaveConfigurada);
        this.chaveSecreta = new SecretKeySpec(chave, ALGORITMO_CHAVE);
    }

    public String criptografar(String valor) {
        if (valor == null) {
            return null;
        }

        try {
            byte[] iv = new byte[TAMANHO_IV_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAMANHO_TAG_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, chaveSecreta, parameterSpec);

            byte[] textoCriptografado = cipher.doFinal(valor.getBytes(StandardCharsets.UTF_8));
            byte[] resultado = combinar(iv, textoCriptografado);

            return PREFIXO_AES_GCM + Base64.getEncoder().encodeToString(resultado);
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao criptografar senha cadastrada", exception);
        }
    }

    public String descriptografar(String valorCriptografado) {
        if (valorCriptografado == null) {
            return null;
        }

        if (!valorCriptografado.startsWith(PREFIXO_AES_GCM)) {
            return descriptografarFormatoLegadoBase64(valorCriptografado);
        }

        try {
            String payloadBase64 = valorCriptografado.substring(PREFIXO_AES_GCM.length());
            byte[] payload = Base64.getDecoder().decode(payloadBase64);

            if (payload.length <= TAMANHO_IV_BYTES) {
                throw new IllegalArgumentException("Payload criptografado inválido");
            }

            byte[] iv = Arrays.copyOfRange(payload, 0, TAMANHO_IV_BYTES);
            byte[] textoCriptografado = Arrays.copyOfRange(payload, TAMANHO_IV_BYTES, payload.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAMANHO_TAG_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, chaveSecreta, parameterSpec);

            byte[] textoOriginal = cipher.doFinal(textoCriptografado);
            return new String(textoOriginal, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao descriptografar senha cadastrada", exception);
        }
    }

    private byte[] carregarChave(String chaveConfigurada) {
        if (chaveConfigurada == null || chaveConfigurada.isBlank()) {
            throw new IllegalStateException(
                    "A variável NEXOVAULT_CRYPTO_KEY não foi configurada. " +
                    "Gere uma chave com: openssl rand -base64 32"
            );
        }

        byte[] chave;

        try {
            chave = Base64.getDecoder().decode(chaveConfigurada.trim());
        } catch (IllegalArgumentException exception) {
            chave = chaveConfigurada.getBytes(StandardCharsets.UTF_8);
        }

        if (chave.length != 16 && chave.length != 24 && chave.length != 32) {
            throw new IllegalStateException(
                    "A chave AES deve possuir 16, 24 ou 32 bytes após decodificação. " +
                    "Recomendado: openssl rand -base64 32"
            );
        }

        return chave;
    }

    private byte[] combinar(byte[] iv, byte[] textoCriptografado) {
        byte[] resultado = new byte[iv.length + textoCriptografado.length];
        System.arraycopy(iv, 0, resultado, 0, iv.length);
        System.arraycopy(textoCriptografado, 0, resultado, iv.length, textoCriptografado.length);
        return resultado;
    }

    private String descriptografarFormatoLegadoBase64(String valorCriptografado) {
        try {
            byte[] decoded = Base64.getDecoder().decode(valorCriptografado);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "Formato de senha criptografada inválido. Esperado AES-GCM com prefixo v1: ou Base64 legado.",
                    exception
            );
        }
    }
}
