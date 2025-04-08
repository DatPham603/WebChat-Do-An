package org.dat.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfiguration {
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("chat-service", p -> p
                        .path("/ws-chat/**")
                        .filters(f -> f
                                .removeRequestHeader("Sec-WebSocket-Protocol"))
                        .uri("lb://CHAT-SERVICE"))
                // Thêm các route khác nếu cần
                .build();
    }

//    @Bean
//    @Primary
//    WebSocketClient tomcatWebSocketClient() {
//        return new TomcatWebSocketClient();
//    }
//
//    @Bean
//    @Primary
//    public RequestUpgradeStrategy requestUpgradeStrategy() {
//        return new TomcatRequestUpgradeStrategy();
//    }
}
