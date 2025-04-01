package org.dat.config;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
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
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        log.info("Before handshake");
        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            // Có thể lấy token từ query param nếu cần
            // token = request.getQueryParams().getFirst("token");
            logger.warn("No Authorization header found or invalid format.");
            return false;
        }

        try {
            JwtParser jwtParser = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build();

            Claims claims = jwtParser.parseClaimsJws(token).getBody();

            attributes.put("userId", claims.getId());
            // Lấy các claim khác nếu cần
            // attributes.put("roles", claims.get("roles"));

            logger.info("Token verified, userId: {}", claims.getSubject());
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token expired.", e);
            return false;
        } catch (UnsupportedJwtException e) {
            logger.warn("Unsupported JWT token.", e);
            return false;
        } catch (MalformedJwtException e) {
            logger.warn("Malformed JWT token.", e);
            return false;
        } catch (SignatureException e) {
            logger.warn("Invalid JWT signature.", e);
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty.", e);
            return false;
        } catch (Exception e) {
            logger.error("Error verifying token.", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // Không cần làm gì ở đây
    }

    private SecretKey getSignKey() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(bytes, SignatureAlgorithm.HS256.getJcaName());
    }
}
