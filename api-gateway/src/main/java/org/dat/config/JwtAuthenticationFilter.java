package org.dat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public JwtAuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Bỏ qua các endpoint không cần auth
            if (config.getExcludePaths().contains(exchange.getRequest().getPath().value())) {
                return chain.filter(exchange);
            }

            // Lấy JWT từ header
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            // Gọi IAM service để validate token
            return webClientBuilder.build()
                    .get()
                    .uri("http://IAM/api/v1/users/validate?token={token}", token)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                            response -> {
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                return Mono.error(new TokenValidationException("Token validation failed")); // Sử dụng TokenValidationException
                            })
                    .bodyToMono(UserInfo.class)
                    .flatMap(userInfo -> {
                        // Thêm thông tin user vào headers
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", userInfo.getId().toString())
                                .header("X-User-Name", userInfo.getUsername())
                                .header("X-User-Roles", String.join(",", userInfo.getRoles()))
                                .build();
                        log.info("userId = {}", userInfo.getId());
                        log.info("username = {}", userInfo.getUsername());
                        log.info("roles = {}", userInfo.getRoles());
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    })
                    .onErrorResume(TokenValidationException.class, e -> {
                        log.info(e.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    })
                    .onErrorResume(e -> {
                        log.error("Error validating token", e);
                        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                        return exchange.getResponse().setComplete();
                    });
        };
    }


    // Custom exception
    private static class TokenValidationException extends RuntimeException {
        public TokenValidationException(String message) {
            super(message);
        }
    }

    public static class Config {
        private List<String> excludePaths;

        // Getter và Setter
        public List<String> getExcludePaths() {
            return excludePaths;
        }

        public void setExcludePaths(List<String> excludePaths) {
            this.excludePaths = excludePaths;
        }
    }

    // DTO cho thông tin user từ IAM service
    public static class UserInfo {
        private UUID id;
        private String username;
        private List<String> roles;

        public UserInfo() {
        }

        // Constructor có tham số
        public UserInfo(UUID id, String username, List<String> roles) {
            this.id = id;
            this.username = username;
            this.roles = roles;
        }

        // Getter và Setter
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}