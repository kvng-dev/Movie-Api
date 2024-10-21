package com.movieflix.movie_api.auth.utils;


import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String username;
    private String password;
}
