package org.dat.config;

import org.dat.entity.Role;
import org.dat.entity.RoleUser;
import org.dat.entity.User;
import org.dat.repository.InvalidTokenRepository;
import org.dat.repository.RoleRepository;
import org.dat.repository.RoleUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtils {
    @Value("${spring.security.authentication.jwt.secret}")
    private String secretKey;
    @Value("${spring.security.authentication.jwt.jwt_refresh_expiration}")
    private Long refreshTokenDuration;
    private final RoleRepository roleRepository;
    private final RoleUserRepository roleUserRepository;
    private final InvalidTokenRepository invalidTokenRepository;

    public JwtTokenUtils(RoleRepository roleRepository,
                         RoleUserRepository roleUserRepository,
                         InvalidTokenRepository invalidTokenRepository) {
        this.roleRepository = roleRepository;
        this.roleUserRepository = roleUserRepository;
        this.invalidTokenRepository = invalidTokenRepository;
    }

    public String generateToken(User user) {
        List<String> roleNames = roleUserRepository.findAllByUserId(user.getId()).stream()
                .map(RoleUser::getRoleId)
                .map(roleId ->
                        roleRepository.findById(roleId).map(Role::getCode)
                                .orElse("Unknow role")).toList();
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeMillis + 86400000);
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());
        claims.put("sub", user.getEmail());
        claims.put("exp", expirationDate);
        claims.put("scope", roleNames);
        claims.put("jti", UUID.randomUUID().toString());
        return  Jwts.builder().claims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generaRefreshToken(User user) {
        List<String> roleNames = roleUserRepository.findAllByUserId(user.getId()).stream()
                .map(RoleUser::getRoleId)
                .map(roleId ->
                        roleRepository.findById(roleId).map(Role::getCode)
                                .orElse("Unknow role")).toList();
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeMillis + refreshTokenDuration);
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());
        claims.put("sub", user.getEmail());
        claims.put("exp", expirationDate);
        claims.put("scope", roleNames);
        claims.put("jti", UUID.randomUUID().toString());
        return  Jwts.builder().claims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims getAllClaimFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String exTractUserName(String token) {
        return getAllClaimFromToken(token).getSubject();
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getSubFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationTimeFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationTimeFromToken(token);
        return expirationDate.before(new Date());
    }

    public String getJtiFromToken(String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    public boolean isTokenValid(String token) {
        try {
            UUID jti = UUID.fromString(getJtiFromToken(token));
            if (invalidTokenRepository.existsById(jti)) {
                if (isTokenExpired(token)) {
                    invalidTokenRepository.deleteById(jti);
                }
                return true;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid JTI : " + e.getMessage());
        }
        return false;
    }

    private SecretKey getSignKey() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(bytes, SignatureAlgorithm.HS256.getJcaName());
    }
}