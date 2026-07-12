package com.microfinance.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Redis-backed sliding-window rate limiter.
 * Limits: auth endpoints 5/min, authenticated users 100/min, anonymous 20/min.
 */
@Component
@Slf4j
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, String> redis;

    public RateLimitFilter(ReactiveRedisTemplate<String, String> redis) { this.redis = redis; }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String ip = getIp(exchange);

        String key;
        int limit;
        if (path.startsWith("/api/v1/auth")) {
            key = "rl:auth:" + ip; limit = 5;
        } else if (userId != null) {
            key = "rl:user:" + userId; limit = 100;
        } else {
            key = "rl:anon:" + ip; limit = 20;
        }

        return redis.opsForValue().increment(key)
                .flatMap(count -> {
                    if (count == 1) return redis.expire(key, Duration.ofMinutes(1)).thenReturn(count);
                    return Mono.just(count);
                })
                .flatMap(count -> {
                    exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(limit));
                    exchange.getResponse().getHeaders().add("X-RateLimit-Remaining",
                            String.valueOf(Math.max(0, limit - count)));
                    if (count > limit) {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        byte[] body = "{\"success\":false,\"message\":\"Rate limit exceeded\"}".getBytes(StandardCharsets.UTF_8);
                        return exchange.getResponse().writeWith(
                                Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
                    }
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> chain.filter(exchange)); // fail-open
    }

    private String getIp(ServerWebExchange exchange) {
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) return xff.split(",")[0].trim();
        return exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() { return -99; }
}
