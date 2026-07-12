package com.microfinance.fraud.rules;

import com.microfinance.fraud.dto.FraudCheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class VelocityRule {

    private final StringRedisTemplate redisTemplate;
    
    // Configurable thresholds could be loaded from FraudRuleRepository
    private static final int MAX_TRANSACTIONS_PER_MINUTE = 5;

    public boolean isViolated(FraudCheckRequest request) {
        String key = "fraud:velocity:user:" + request.getUserId();
        
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        return count != null && count > MAX_TRANSACTIONS_PER_MINUTE;
    }
}
