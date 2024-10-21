package com.movieflix.movie_api.auth.service;

import com.movieflix.movie_api.auth.entity.RefreshToken;
import com.movieflix.movie_api.auth.entity.User;
import com.movieflix.movie_api.auth.repository.RefreshTokenRepository;
import com.movieflix.movie_api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username)
        );
        RefreshToken refreshToken = user.getRefreshToken();
        if (refreshToken == null) {
            new RefreshToken();
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(5*60*60*10000))
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token).orElseThrow(
                () -> new RuntimeException("Token not found")
        );
        if(refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh Token expired");
        }
        return refreshToken;
    }
}
