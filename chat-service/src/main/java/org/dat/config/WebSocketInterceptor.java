package org.dat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        log.info("beforeHandshake request: {}", request);

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String userId = servletRequest.getServletRequest().getHeader("X-User-Id");
            String username = servletRequest.getServletRequest().getHeader("X-User-Name");
            String roles = servletRequest.getServletRequest().getHeader("X-User-Roles");

            log.info("userId: {}", userId);
            log.info("username: {}", username);
            log.info("roles: {}", roles);

            if (userId != null && !userId.isEmpty() &&
                    username != null && !username.isEmpty()
                    && roles != null && !roles.isEmpty()) {
                attributes.put("userId", userId);
                attributes.put("username", username);
                attributes.put("roles", roles);
                return true;
            }
        }

        log.info("Handshake failed, user not authenticated.");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        if (exception != null) {
            log.error("Handshake failed: {}", exception.getMessage());
        }
    }
}