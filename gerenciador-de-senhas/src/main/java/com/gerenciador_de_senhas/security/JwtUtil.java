package com.gerenciador_de_senhas.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerenciador_de_senhas.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String HMAC_ALGORITMO = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final ObjectMapper objectMapper;
    private final byte[] segredo;
    private final long accessExpiracaoMinutos;
    private final long mfaExpiracaoMinutos;

    public JwtUtil(ObjectMapper objectMapper,
                   @Value("${app.jwt.secret}") String segredo,
                   @Value("${app.jwt.access-exp-minutes:60}") long accessExpiracaoMinutos,
                   @Value("${app.jwt.mfa-exp-minutes:5}") long mfaExpiracaoMinutos) {
        if (segredo == null || segredo.isBlank()) {
            throw new IllegalStateException("A variável NEXOVAULT_JWT_SECRET não foi configurada.");
        }

        this.objectMapper = objectMapper;
        this.segredo = segredo.getBytes(StandardCharsets.UTF_8);
        this.accessExpiracaoMinutos = accessExpiracaoMinutos;
        this.mfaExpiracaoMinutos = mfaExpiracaoMinutos;
    }

    public String gerarAccessToken(Usuario usuario) {
        return gerarToken(usuario, "access", accessExpiracaoMinutos);
    }

    public String gerarMfaToken(Usuario usuario) {
        return gerarToken(usuario, "mfa", mfaExpiracaoMinutos);
    }

    public JwtClaims validarAccessToken(String token) {
        return validarToken(token, "access");
    }

    public JwtClaims validarMfaToken(String token) {
        return validarToken(token, "mfa");
    }

    private String gerarToken(Usuario usuario, String tipo, long expiracaoMinutos) {
        try {
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            long agora = Instant.now().getEpochSecond();
            long expiracao = agora + (expiracaoMinutos * 60);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", String.valueOf(usuario.getId()));
            payload.put("email", usuario.getEmail());
            payload.put("type", tipo);
            payload.put("iat", agora);
            payload.put("exp", expiracao);

            String headerBase64 = base64Url(objectMapper.writeValueAsBytes(header));
            String payloadBase64 = base64Url(objectMapper.writeValueAsBytes(payload));
            String conteudo = headerBase64 + "." + payloadBase64;
            String assinatura = assinar(conteudo);

            return conteudo + "." + assinatura;
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao gerar token JWT", exception);
        }
    }

    private JwtClaims validarToken(String token, String tipoEsperado) {
        try {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Token ausente");
            }

            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                throw new IllegalArgumentException("Token inválido");
            }

            String conteudo = partes[0] + "." + partes[1];
            String assinaturaEsperada = assinar(conteudo);

            if (!assinaturaEsperada.equals(partes[2])) {
                throw new IllegalArgumentException("Assinatura do token inválida");
            }

            byte[] payloadBytes = BASE64_URL_DECODER.decode(partes[1]);
            Map<String, Object> payload = objectMapper.readValue(
                    payloadBytes,
                    new TypeReference<Map<String, Object>>() {}
            );

            String tipo = String.valueOf(payload.get("type"));
            if (!tipoEsperado.equals(tipo)) {
                throw new IllegalArgumentException("Tipo de token inválido");
            }

            long expiracao = Long.parseLong(String.valueOf(payload.get("exp")));
            if (Instant.now().getEpochSecond() > expiracao) {
                throw new IllegalArgumentException("Token expirado");
            }

            Long usuarioId = Long.valueOf(String.valueOf(payload.get("sub")));
            String email = String.valueOf(payload.get("email"));

            return new JwtClaims(usuarioId, email, tipo);
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Token inválido", exception);
        }
    }

    private String assinar(String conteudo) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITMO);
            mac.init(new SecretKeySpec(segredo, HMAC_ALGORITMO));
            byte[] assinatura = mac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8));
            return base64Url(assinatura);
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao assinar token JWT", exception);
        }
    }

    private String base64Url(byte[] bytes) {
        return BASE64_URL_ENCODER.encodeToString(bytes);
    }
}
