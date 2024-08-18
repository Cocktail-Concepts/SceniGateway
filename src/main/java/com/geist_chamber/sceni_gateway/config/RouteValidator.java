package com.geist_chamber.sceni_gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {


    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/geists/auth/**"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> pathMatcher.match(uri,request.getURI().getPath()));

}
