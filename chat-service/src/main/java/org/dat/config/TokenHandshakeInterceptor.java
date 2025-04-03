package org.dat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
@Slf4j
public class TokenHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TokenHandshakeInterceptor.class);

    @Value("${spring.authentication.jwt.secret}")
    private String secretKey;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) {
        logger.info("before handshake");
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = request.getHeaders().getFirst("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                // Có thể lấy token từ query param nếu cần
                String query = servletRequest.getServletRequest().getQueryString();
                if (query != null) {
                    MultiValueMap<String, String> queryParams = UriComponentsBuilder
                            .fromUriString("?" + query).build().getQueryParams();
                    token = queryParams.getFirst("token");
                }

                if (token == null) {
                    logger.warn("No Authorization header found or invalid format.");
                    return false;
                }
            }

            JwtParser jwtParser = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build();

            Claims claims = jwtParser.parseClaimsJws(token).getBody();

            attributes.put("userId", claims.getId());
            attributes.put("email", claims.getSubject());
            attributes.put("username", claims.get("username"));
            logger.info("attributes : {}", attributes);
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
