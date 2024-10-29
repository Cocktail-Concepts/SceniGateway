package com.geist_chamber.sceni_gateway.security;

import com.geist_chamber.sceni_gateway.config.RouteValidator;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {

    private final RouteValidator routerValidator;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationFilter(RouteValidator routerValidator, JwtUtil jwtUtil) {
        this.routerValidator = routerValidator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if (this.isAuthMissing(request)) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED, "Authorization header is missing");
            }

            final String token = this.getAuthHeader(request).substring(7);

            try {
                if (!jwtUtil.validateToken(token)) {
                    return this.onError(exchange, HttpStatus.FORBIDDEN, "Invalid JWT token");
                }
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                // Handle expired token case
                return this.onError(exchange, HttpStatus.UNAUTHORIZED, "JWT token has expired");
            } catch (Exception e) {
                // Handle any other exceptions related to JWT validation
                return this.onError(exchange, HttpStatus.FORBIDDEN, "JWT token validation failed");
            }

            this.updateRequest(exchange, token);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus, String errorMessage) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");

        DataBuffer dataBuffer = response.bufferFactory().wrap(("{\"error\": \"" + errorMessage + "\"}").getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }


    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").getFirst();
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private void updateRequest(ServerWebExchange exchange, String token) {
        Claims claims = jwtUtil.extractAllClaims(token); // Extract claims from the token

        String username = claims.getSubject();

        exchange.getRequest().mutate()
                .header("username", username)
                .build();
    }


}
