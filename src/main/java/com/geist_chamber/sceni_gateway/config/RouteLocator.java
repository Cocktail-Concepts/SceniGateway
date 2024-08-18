package com.geist_chamber.sceni_gateway.config;

import com.geist_chamber.sceni_gateway.security.AuthenticationFilter;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
public class RouteLocator {
    private final AuthenticationFilter filter;

    public RouteLocator(AuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public org.springframework.cloud.gateway.route.RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("GEIST_SERVICE", r -> r.path("/api/v1/geists/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://localhost:8081"))

                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth-service"))
                .build();
    }
}
