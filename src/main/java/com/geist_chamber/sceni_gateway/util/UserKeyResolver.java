package com.geist_chamber.sceni_gateway.util;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
//
//@Component
//public class UserKeyResolver implements KeyResolver {
//
//    @Override
//    public Mono<String> resolve(ServerWebExchange exchange) {
//        // Extract the user email from the request headers added by AuthenticationFilter
//        System.out.println(exchange.getRequest().getHeaders().getFirst("username")+"--------ema");
//        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("username"));
//    }
//}
