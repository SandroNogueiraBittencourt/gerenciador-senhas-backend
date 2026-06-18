package com.gerenciador_de_senhas.util;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;

@Component
public class TotpUtil {

    private static final String HMAC_ALGORITMO = "HmacSHA1";
    private static final String BASE32_ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int TAMANHO_SECRET_BYTES = 20;
    private static final int PERIODO_SEGUNDOS = 30;
    private static final int DIGITOS = 6;

    private final SecureRandom secureRandom = new SecureRandom();

    public String gerarSecret() {
        byte[] bytes = new byte[TAMANHO_SECRET_BYTES];
        secureRandom.nextBytes(bytes);
        return codificarBase32(bytes);
    }

    public boolean validarCodigo(String secretBase32, String codigo) {
        if (secretBase32 == null || secretBase32.isBlank() || codigo == null || !codigo.matches("^[0-9]{6}$")) {
            return false;
        }

        long contadorAtual = Instant.now().getEpochSecond() / PERIODO_SEGUNDOS;

        for (long deslocamento = -1; deslocamento <= 1; deslocamento++) {
            String codigoEsperado = gerarCodigo(secretBase32, contadorAtual + deslocamento);

            if (codigo.equals(codigoEsperado)) {
                return true;
            }
        }

        return false;
    }

    public String gerarOtpAuthUri(String issuer, String email, String secretBase32) {
        String label = issuer + ":" + email;
        String labelEncoded = URLEncoder.encode(label, StandardCharsets.UTF_8).replace("+", "%20");
        String issuerEncoded = URLEncoder.encode(issuer, StandardCharsets.UTF_8).replace("+", "%20");

        return "otpauth://totp/" + labelEncoded
                + "?secret=" + secretBase32
                + "&issuer=" + issuerEncoded
                + "&algorithm=SHA1"
                + "&digits=" + DIGITOS
                + "&period=" + PERIODO_SEGUNDOS;
    }

    private String gerarCodigo(String secretBase32, long contador) {
        try {
            byte[] chave = decodificarBase32(secretBase32);
            byte[] contadorBytes = ByteBuffer.allocate(8).putLong(contador).array();

            Mac mac = Mac.getInstance(HMAC_ALGORITMO);
            mac.init(new SecretKeySpec(chave, HMAC_ALGORITMO));
            byte[] hash = mac.doFinal(contadorBytes);

            int offset = hash[hash.length - 1] & 0x0F;
            int binario = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            int otp = binario % 1_000_000;
            return String.format("%06d", otp);
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao gerar código TOTP", exception);
        }
    }

    private String codificarBase32(byte[] dados) {
        StringBuilder resultado = new StringBuilder();
        int buffer = 0;
        int bitsNoBuffer = 0;

        for (byte valor : dados) {
            buffer = (buffer << 8) | (valor & 0xFF);
            bitsNoBuffer += 8;

            while (bitsNoBuffer >= 5) {
                int indice = (buffer >> (bitsNoBuffer - 5)) & 0x1F;
                resultado.append(BASE32_ALFABETO.charAt(indice));
                bitsNoBuffer -= 5;
            }
        }

        if (bitsNoBuffer > 0) {
            int indice = (buffer << (5 - bitsNoBuffer)) & 0x1F;
            resultado.append(BASE32_ALFABETO.charAt(indice));
        }

        return resultado.toString();
    }

    private byte[] decodificarBase32(String valor) {
        String normalizado = valor.replace("=", "").replace(" ", "").toUpperCase();
        byte[] bytes = new byte[normalizado.length() * 5 / 8];
        int buffer = 0;
        int bitsNoBuffer = 0;
        int indiceByte = 0;

        for (char caractere : normalizado.toCharArray()) {
            int indice = BASE32_ALFABETO.indexOf(caractere);
            if (indice < 0) {
                throw new IllegalArgumentException("Secret Base32 inválido");
            }

            buffer = (buffer << 5) | indice;
            bitsNoBuffer += 5;

            if (bitsNoBuffer >= 8) {
                bytes[indiceByte++] = (byte) ((buffer >> (bitsNoBuffer - 8)) & 0xFF);
                bitsNoBuffer -= 8;
            }
        }

        return Arrays.copyOf(bytes, indiceByte);
    }
}
