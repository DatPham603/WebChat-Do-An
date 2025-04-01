package org.dat.config;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

    @Component
    @Slf4j
    public class TokenHandshakeInterceptor implements HandshakeInterceptor {

        private static final Logger logger = LoggerFactory.getLogger(TokenHandshakeInterceptor.class);

        private String secretKey = "yPTyD4NJtTyXxf9v+Y9bPerZs6XtiCyD+fNdlB/lRmdq4UrpOK6brnicDMZXbgiq"; // Replace with your secret key

        @Override
        public boolean beforeHandshake(
                ServerHttpRequest request, ServerHttpResponse response,
                WebSocketHandler wsHandler, Map<String, Object> attributes) {
            logger.info("before handshake");
            if (request instanceof ServletServerHttpRequest) {
                String token = request.getHeaders().getFirst("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                } else {
                    // Có thể lấy token từ query param nếu cần
                    // token = request.getQueryParams().getFirst("token");
                    logger.warn("No Authorization header found or invalid format.");
                    return false;
                }

                JwtParser jwtParser = Jwts.parser()
                        .verifyWith(getSignKey())
                        .build();

                Claims claims = jwtParser.parseClaimsJws(token).getBody();

                attributes.put("userId", claims.getId());
                attributes.put("username", claims.getSubject());
                logger.info("token : {}", token);
            }
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Exception exception) {
        }

        private SecretKey getSignKey() {
            byte[] bytes = Base64.getDecoder().decode(secretKey);
            return new SecretKeySpec(bytes, SignatureAlgorithm.HS256.getJcaName());
        }
    }
