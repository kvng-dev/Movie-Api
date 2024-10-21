package com.movieflix.movie_api.repository;

import com.movieflix.movie_api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
