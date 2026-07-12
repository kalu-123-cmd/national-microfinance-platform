package com.microfinance.fraud.service;

import com.microfinance.fraud.dto.FraudCheckRequest;
import com.microfinance.fraud.dto.FraudCheckResponse;
import com.microfinance.fraud.entity.FraudAlert;

import java.util.List;

public interface FraudDetectionService {
    FraudCheckResponse evaluateTransaction(FraudCheckRequest request);
    void evaluateTransactionAsync(FraudCheckRequest request);
    List<FraudAlert> getAlertsByUser(String userId);
    List<FraudAlert> getAlertsByTransaction(String transactionId);
    FraudAlert updateAlertStatus(Long alertId, String status);
}