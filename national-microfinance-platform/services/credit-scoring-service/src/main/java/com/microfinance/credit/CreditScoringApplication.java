package com.microfinance.credit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {"com.microfinance.credit", "com.microfinance.security"})
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableKafka
public class CreditScoringApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreditScoringApplication.class, args);
    }
}
