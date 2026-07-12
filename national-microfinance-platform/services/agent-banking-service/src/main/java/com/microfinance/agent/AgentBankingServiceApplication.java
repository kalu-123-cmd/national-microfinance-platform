package com.microfinance.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.microfinance.agent", "com.microfinance.security"})
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableKafka
@EnableScheduling
public class AgentBankingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentBankingServiceApplication.class, args);
    }
}
