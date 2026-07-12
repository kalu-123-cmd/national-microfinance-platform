package com.microfinance.literacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.microfinance.literacy", "com.microfinance.security"})
@EnableDiscoveryClient
public class FinancialLiteracyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinancialLiteracyServiceApplication.class, args);
    }
}
