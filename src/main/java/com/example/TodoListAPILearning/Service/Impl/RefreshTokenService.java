package com.example.TodoListAPILearning.Service.Impl;

import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.RefreshToken;
import com.example.TodoListAPILearning.Repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final long refreshTokenDurations = 7 * 24 * 60 * 60 * 100;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(AppUser appUser) {
        RefreshToken token = new RefreshToken();
        token.setUser(appUser);
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurations));
        token.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token is expired. Please login again.");
        }

        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
