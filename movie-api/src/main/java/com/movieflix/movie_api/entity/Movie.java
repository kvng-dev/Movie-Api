package com.movieflix.movie_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @NotBlank(message = "Movie title is mandatory")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Director name is mandatory")
    @Column(nullable = false)
    private String director;

    @NotBlank(message = "Studio name is mandatory")
    @Column(nullable = false)
    private String studio;

    @Column(nullable = false)
    private Integer releaseYear;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @NotBlank(message = "Movie poster is mandatory")
    @Column(nullable = false)
    private String poster;
}
