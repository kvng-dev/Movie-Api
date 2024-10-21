package com.movieflix.movie_api.auth.utils;

import lombok.Data;

@Data
public class RefreshTokenRequest
{
    private String refreshToken;
}
