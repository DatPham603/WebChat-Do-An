package org.dat.service;

import org.dat.config.JwtTokenUtils;
import org.dat.entity.RefreshToken;
import org.dat.repository.RefreshTokenRepository;
import org.dat.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${spring.security.authentication.jwt.jwt_refresh_expiration}")
    private Long refreshTokenDuration;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    public RefreshToken createRefreshToken(UUID userId, UUID accessTokenId, Date accessTokenExp) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + refreshTokenDuration));
        refreshToken.setRefreshToken(jwtTokenUtils.generaRefreshToken(userRepository.findById(userId).get()));
        refreshToken.setAccessTokenId(accessTokenId);
        refreshToken.setAccessTokenExp(accessTokenExp);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(RefreshToken token) {
        if (jwtTokenUtils.getExpirationTimeFromToken(token.getRefreshToken()).before(new Date())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Token was expired");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}