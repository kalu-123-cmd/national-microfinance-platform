package com.microfinance.credit.service;

import com.microfinance.credit.dto.CreditScoreRequest;
import com.microfinance.credit.dto.CreditScoreResponse;

public interface CreditScoringService {
    CreditScoreResponse calculateScore(CreditScoreRequest request);
    CreditScoreResponse getScore(String userId);
}