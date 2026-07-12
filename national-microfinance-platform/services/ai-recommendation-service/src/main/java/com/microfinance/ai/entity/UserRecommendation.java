package com.microfinance.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "user_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRecommendation {
    @Id
    private String id;
    private String userId;
    private String productType;
    private String productName;
    private String description;
    private Double confidenceScore;
    private String actionUrl;
    private Instant createdAt;
    private Instant expiresAt;
    private String status; // ACTIVE, EXPIRED, ACCEPTED
}