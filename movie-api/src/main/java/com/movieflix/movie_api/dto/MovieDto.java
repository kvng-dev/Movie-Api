package com.movieflix.movie_api.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

    private Integer movieId;

    @NotBlank(message = "Movie title is mandatory")
    private String title;

    @NotBlank(message = "Director name is mandatory")
    private String director;

    @NotBlank(message = "Studio name is mandatory")
    private String studio;

    private Integer releaseYear;

    private Set<String> movieCast;

    @NotBlank(message = "Movie poster is mandatory")
    private String poster;

    @NotBlank(message = "Movie poster url is mandatory")
    private String posterUrl;
}
