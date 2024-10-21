package com.movieflix.movie_api.service;

import com.movieflix.movie_api.dto.MovieDto;
import com.movieflix.movie_api.dto.MoviePageResponse;
import com.movieflix.movie_api.entity.Movie;
import com.movieflix.movie_api.exceptions.FileExistsException;
import com.movieflix.movie_api.exceptions.MovieNotFoundException;
import com.movieflix.movie_api.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImp implements MovieService{

    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException, RuntimeException {
       //upload file
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File already exists, please upload a different file");
        }
       String uploadFile = fileService.uploadFile(path, file);

       //set the value of field poster as filename
        movieDto.setPoster(uploadFile);

        //map dto to movie object
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getReleaseYear(),
                movieDto.getMovieCast(),
                movieDto.getPoster()
        );

        //save movie object to database
        Movie savedMovie = movieRepository.save(movie);

        //generate posterUrl
        String posterUrl = baseUrl + "/file/" + uploadFile;

        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getReleaseYear(),
                savedMovie.getMovieCast(),
                savedMovie.getPoster(),
                posterUrl
        );
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(
                () -> new MovieNotFoundException("Movie not found with id: " + movieId
                ));

        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getReleaseYear(),
                movie.getMovieCast(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {

        Movie mv = movieRepository.findById(movieId).orElseThrow(
                () -> new MovieNotFoundException("Movie not found with id: " + movieId
                ));

        String fileName = mv.getPoster();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
             fileName = fileService.uploadFile(path, file);
        }

        movieDto.setPoster(fileName);

        Movie movie = new Movie(
                mv.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getReleaseYear(),
                movieDto.getMovieCast(),
                movieDto.getPoster()
        );

        movieRepository.save(movie);

        String posterUrl = baseUrl + "/file/" + fileName;

        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getReleaseYear(),
                movie.getMovieCast(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        Movie movie = movieRepository.findById(movieId).orElseThrow(
                () -> new MovieNotFoundException("Movie not found with id: " + movieId));

        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));
        movieRepository.delete(movie);
        return "Successfully deleted movie with id: " + movieId;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            movieDtos.add(new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getReleaseYear(),
                    movie.getMovieCast(),
                    movie.getPoster(),
                    posterUrl
            ));
        }
        return movieDtos;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Movie> moviesPages = movieRepository.findAll(pageable);
        List<Movie> movies = moviesPages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            movieDtos.add(new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getReleaseYear(),
                    movie.getMovieCast(),
                    movie.getPoster(),
                    posterUrl
            ));
        }

        return new MoviePageResponse(
                movieDtos,
                pageNumber, pageSize,
                moviesPages.getTotalElements(),
                moviesPages.getTotalPages(),
                moviesPages.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Movie> moviesPages = movieRepository.findAll(pageable);
        List<Movie> movies = moviesPages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            movieDtos.add(new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getReleaseYear(),
                    movie.getMovieCast(),
                    movie.getPoster(),
                    posterUrl
            ));
        }

        return new MoviePageResponse(
                movieDtos,
                pageNumber, pageSize,
                moviesPages.getTotalElements(),
                moviesPages.getTotalPages(),
                moviesPages.isLast());
    }
}
