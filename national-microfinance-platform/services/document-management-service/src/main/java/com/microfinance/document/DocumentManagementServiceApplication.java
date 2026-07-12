package com.microfinance.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.microfinance.document", "com.microfinance.security"})
@EnableDiscoveryClient
public class DocumentManagementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocumentManagementServiceApplication.class, args);
    }
}
