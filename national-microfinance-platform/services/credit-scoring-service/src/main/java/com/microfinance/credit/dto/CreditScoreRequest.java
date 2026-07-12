package com.microfinance.credit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditScoreRequest {
    private String userId;
    // Potentially additional data to perform live scoring
    // e.g. income, expenses, number of active loans, etc.
}
