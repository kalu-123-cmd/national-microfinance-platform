package com.microfinance.credit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditScoreResponse {
    private String userId;
    private Integer score;
    private String rating;
    private Double defaultProbability;
    private String timestamp;
}
