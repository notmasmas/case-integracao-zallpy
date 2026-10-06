package com.branch_master.ecovolt360.auth.infrastructure.security;

import com.branch_master.ecovolt360.auth.application.port.TokenIssuer;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
public class JwtTokenIssuer implements TokenIssuer {

    private final byte[] secret;
    private final long expirationMinutes;

    public JwtTokenIssuer(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 bytes");
        }
        this.secret = secretBytes;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public String issue(UUID userId, Role role) {
        Instant now = Instant.now();
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = base64Url(("{\"sub\":\"" + userId + "\",\"role\":\"" + role.name()
                + "\",\"iat\":" + now.getEpochSecond()
                + ",\"exp\":" + now.plusSeconds(expirationMinutes * 60).getEpochSecond() + "}")
                .getBytes(StandardCharsets.UTF_8));
        String content = header + "." + payload;
        return content + "." + base64Url(sign(content));
    }

    private byte[] sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not sign access token", exception);
        }
    }

    private static String base64Url(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }
}
