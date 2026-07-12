package com.microfinance.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGrowthResponse {
    private LocalDate date;
    private Long newUsers;
    private Long activeUsers;
    private Long verifiedUsers;
}