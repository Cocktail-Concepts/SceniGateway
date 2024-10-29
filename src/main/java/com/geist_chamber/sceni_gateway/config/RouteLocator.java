package com.geist_chamber.sceni_gateway.config;

import com.geist_chamber.sceni_gateway.security.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;


@Configuration
@EnableDiscoveryClient
public class RouteLocator {
    private final AuthenticationFilter filter;
//    private final UserKeyResolver userKeyResolver;
    @Value("${ec2uri}")
    private String ec2uri;

    public RouteLocator(AuthenticationFilter filter) {
        this.filter = filter;
//        this.userKeyResolver = userKeyResolver;
    }


    @Bean
    public org.springframework.cloud.gateway.route.RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("GEIST_SERVICE", r -> r.path("/api/v1/geists/**")
                        .filters(f -> f.filter(filter))
                        .uri(ec2uri+":8081"))
                .route("MIND_SNIPPET_SERVICE", r -> r.path("/api/v1/mind-snippets/**")
                        .filters(f -> f.filter(filter).requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                .setKeyResolver(userKeyResolver())))
                        .uri(ec2uri+":8083"))
                .build();
    }
    
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("username"));};
    }
//    @Bean
//    public org.springframework.cloud.gateway.route.RouteLocator routes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("GEIST_SERVICE", r -> r.path("/api/v1/geists/**")
//                        .filters(f -> f.filter(filter))
//                        .uri("lb://GEIST_SERVICE"))  // Use Eureka discovery
//                .route("MIND_SNIPPET_SERVICE", r -> r.path("/api/v1/mind-snippets/**")
//                        .filters(f -> f.filter(filter))
//                        .uri("lb://MIND_SNIPPET_SERVICE"))  // Use Eureka discovery
//                .build();
//    }
    @Bean
    public DiscoveryClientRouteDefinitionLocator discoveryClientRouteDefinitionLocator(
            ReactiveDiscoveryClient discoveryClient, DiscoveryLocatorProperties properties) {
        properties.setEnabled(true);
        return new DiscoveryClientRouteDefinitionLocator(discoveryClient, properties);
    }
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(2, 3);
    }

//    @Bean
//    public KeyResolver userKeyResolver() {
//        return new UserKeyResolver(); // Custom user resolver based on your auth filter
//    }
}
