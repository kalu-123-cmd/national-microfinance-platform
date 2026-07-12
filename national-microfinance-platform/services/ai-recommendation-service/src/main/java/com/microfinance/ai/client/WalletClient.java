package com.microfinance.ai.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "wallet-service", path = "/api/v1/wallets")
public interface WalletClient {
    
    @GetMapping("/user/{userId}/balance")
    BigDecimal getWalletBalance(@PathVariable("userId") String userId);
}
