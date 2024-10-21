package com.movieflix.movie_api.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;
    @Column(nullable = false, length = 500)
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    @Column(nullable = false)
    private Instant expiryDate;

    @OneToOne
    private User user;
}
