package com.microfinance.fraud.service;

import com.microfinance.fraud.dto.FraudCheckRequest;
import com.microfinance.fraud.dto.FraudCheckResponse;
import com.microfinance.fraud.entity.FraudAlert;
import com.microfinance.fraud.repository.FraudAlertRepository;
import com.microfinance.fraud.rules.FraudRuleEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private final FraudRuleEvaluator ruleEvaluator;
    private final FraudAlertRepository alertRepository;

    @Override
    @Transactional
    public FraudCheckResponse evaluateTransaction(FraudCheckRequest request) {
        log.info("Evaluating transaction for fraud: {}", request.getTransactionId());
        
        FraudCheckResponse response = ruleEvaluator.evaluate(request);

        if (response.isFraudulent()) {
            log.warn("Fraud detected for transaction {}: {}", request.getTransactionId(), response.getTriggeredRules());
            
            FraudAlert alert = FraudAlert.builder()
                    .transactionId(request.getTransactionId())
                    .userId(request.getUserId())
                    .ruleViolated(String.join(",", response.getTriggeredRules()))
                    .description("Fraud detected during synchronous check")
                    .riskLevel(response.getRiskLevel())
                    .status("INVESTIGATING")
                    .build();
            alertRepository.save(alert);
        }

        return response;
    }

    @Override
    @Transactional
    public void evaluateTransactionAsync(FraudCheckRequest request) {
        log.info("Asynchronously evaluating transaction for fraud: {}", request.getTransactionId());
        
        FraudCheckResponse response = ruleEvaluator.evaluate(request);

        if (response.isFraudulent()) {
            log.warn("Async Fraud detected for transaction {}: {}", request.getTransactionId(), response.getTriggeredRules());
            
            FraudAlert alert = FraudAlert.builder()
                    .transactionId(request.getTransactionId())
                    .userId(request.getUserId())
                    .ruleViolated(String.join(",", response.getTriggeredRules()))
                    .description("Fraud detected during asynchronous analysis")
                    .riskLevel(response.getRiskLevel())
                    .status("INVESTIGATING")
                    .build();
            alertRepository.save(alert);
            
            // In a real scenario, we might publish an event to block the account here
        }
    }

    @Override
    public List<FraudAlert> getAlertsByUser(String userId) {
        return alertRepository.findByUserId(userId);
    }

    @Override
    public List<FraudAlert> getAlertsByTransaction(String transactionId) {
        return alertRepository.findByTransactionId(transactionId);
    }

    @Override
    @Transactional
    public FraudAlert updateAlertStatus(Long alertId, String status) {
        Optional<FraudAlert> alertOpt = alertRepository.findById(alertId);
        if (alertOpt.isPresent()) {
            FraudAlert alert = alertOpt.get();
            alert.setStatus(status);
            return alertRepository.save(alert);
        }
        throw new RuntimeException("Alert not found with id: " + alertId);
    }
}
