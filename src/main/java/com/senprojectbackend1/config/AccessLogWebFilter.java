package com.senprojectbackend1.config;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(-1) // Pour être exécuté tôt
public class AccessLogWebFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger("ACCESS_LOG");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long start = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        return chain
            .filter(exchange)
            .doFinally(signalType -> {
                ServerHttpResponse response = exchange.getResponse();
                Map<String, Object> logMap = new HashMap<>();
                logMap.put("method", request.getMethod() != null ? request.getMethod().name() : null);
                logMap.put("uri", request.getURI().getPath());
                logMap.put("query", request.getURI().getQuery());
                logMap.put("status", response.getStatusCode() != null ? response.getStatusCode().value() : 0);
                logMap.put("durationMs", System.currentTimeMillis() - start);
                logMap.put(
                    "remoteAddr",
                    request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : null
                );
                logMap.put("userAgent", request.getHeaders().getFirst("User-Agent"));
                // Ajoute le traceId si présent dans le MDC
                String traceId = MDC.get("traceId");
                if (traceId != null) {
                    logMap.put("traceId", traceId);
                }
                log.info("HTTP_ACCESS {}", logMap);
            });
    }
}
