//package org.dat.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//@Configuration
//@EnableWebSocket
//@RequiredArgsConstructor
//public class FileWebSocketConfig implements WebSocketConfigurer {
//
//    private final FileImageWebSocketHandler fileImageWebSocketHandler;
//    private final TokenHandshakeInterceptor tokenHandshakeInterceptor;
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(fileImageWebSocketHandler, "/ws-files")
//                .addInterceptors(tokenHandshakeInterceptor)
//                .setAllowedOriginPatterns("*");
//    }
//}
