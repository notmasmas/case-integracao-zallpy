package com.branch_master.ecovolt360.auth.infrastructure.security;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.application.port.TokenVerifier;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
public class JwtTokenVerifier implements TokenVerifier {

    private final byte[] secret;
    private final ObjectMapper objectMapper;

    public JwtTokenVerifier(
            @Value("${app.jwt.secret}") String secret,
            ObjectMapper objectMapper
    ) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.objectMapper = objectMapper;
    }

    @Override
    public AuthenticatedUser verify(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new InvalidTokenException();
        }

        try {
            byte[] expected = sign(parts[0] + "." + parts[1]);
            byte[] received = Base64.getUrlDecoder().decode(parts[2]);
            if (!MessageDigest.isEqual(expected, received)) {
                throw new InvalidTokenException();
            }

            JsonNode payload = objectMapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
            if (payload.path("exp").asLong() <= Instant.now().getEpochSecond()) {
                throw new InvalidTokenException();
            }

            return new AuthenticatedUser(
                    UUID.fromString(payload.path("sub").asString()),
                    Role.valueOf(payload.path("role").asString())
            );
        } catch (InvalidTokenException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new InvalidTokenException();
        }
    }

    private byte[] sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not verify access token", exception);
        }
    }
}
