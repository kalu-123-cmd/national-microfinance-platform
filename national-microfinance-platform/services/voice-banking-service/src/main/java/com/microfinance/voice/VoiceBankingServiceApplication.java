package com.microfinance.voice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.microfinance.voice", "com.microfinance.security"})
@EnableDiscoveryClient
@EnableJpaAuditing
public class VoiceBankingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VoiceBankingServiceApplication.class, args);
    }
}
