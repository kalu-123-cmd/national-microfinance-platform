package com.microfinance.event.fraud;

import com.microfinance.event.BaseEvent;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @EqualsAndHashCode(callSuper = true)
public class FraudAlertEvent extends BaseEvent {
    private String alertId;
    private String userId;
    private String transactionId;
    private double riskScore;
    private String riskLevel;  // LOW, MEDIUM, HIGH, CRITICAL
    private String reason;
    private BigDecimal transactionAmount;
    private boolean blocked;

    public FraudAlertEvent(String alertId, String userId, String transactionId,
                            double riskScore, String riskLevel, String reason) {
        super("FRAUD_ALERT_RAISED", "fraud-detection-service");
        this.alertId = alertId; this.userId = userId;
        this.transactionId = transactionId; this.riskScore = riskScore;
        this.riskLevel = riskLevel; this.reason = reason;
    }
}
