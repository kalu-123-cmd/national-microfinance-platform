package com.microfinance.auth.dto;

import lombok.*;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {

    private String userId;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn; // seconds
    private Instant issuedAt;
    private Instant expiresAt;
    private String[] scopes;
    private String sessionId;
}
