package com.microfinance.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway — single entry point for all client traffic.
 *
 * Responsibilities:
 *  - JWT authentication (validates token, passes X-User-Id header downstream)
 *  - Rate limiting (Redis token bucket per user/IP)
 *  - Dynamic routing via Eureka service registry (lb:// URIs)
 *  - Circuit breaking (Resilience4j) with fallback responses
 *  - CORS handling
 *  - Distributed tracing header propagation (W3C TraceContext)
 *  - Swagger aggregation from all downstream services
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
