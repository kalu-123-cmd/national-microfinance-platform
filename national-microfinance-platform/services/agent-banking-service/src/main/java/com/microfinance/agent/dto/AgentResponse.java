package com.microfinance.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentResponse {
    private String id;
    private String userId;
    private String businessName;
    private String businessId;
    private String phoneNumber;
    private String email;
    private String region;
    private String woreda;
    private String kebele;
    private Double latitude;
    private Double longitude;
    private String status;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
    private BigDecimal dailyProcessed;
    private BigDecimal monthlyProcessed;
    private BigDecimal balance;
    private Instant createdAt;
}