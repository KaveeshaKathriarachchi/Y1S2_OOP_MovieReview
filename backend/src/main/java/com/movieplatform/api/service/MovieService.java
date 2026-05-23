package com.movieplatform.api.service;

import com.movieplatform.api.model.Movie;
import com.movieplatform.api.repository.MovieRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final UserService userService;

    public MovieService(MovieRepository movieRepository, UserService userService) {
        this.movieRepository = movieRepository;
        this.userService = userService;
    }

    public List<Movie> allMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovie(String imdbId) {
        return movieRepository.findById(imdbId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
    }

    public List<Movie> byGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    public List<Movie> byReleaseDate(LocalDate releaseDate) {
        return movieRepository.findByReleaseDate(releaseDate);
    }

    public Movie movieForPaidViewer(String imdbId, String userId) {
        if (!userService.getUser(userId).isPaidUser()) {
            throw new IllegalArgumentException("Only paid users can watch full movies");
        }
        return getMovie(imdbId);
    }

    public Movie addMovie(String adminUserId, Movie movie) {
        userService.requireAdmin(adminUserId);
        if (movieRepository.existsById(movie.getImdbId())) {
            throw new IllegalArgumentException("Movie already exists");
        }
        return movieRepository.save(movie);
    }

    public Movie updateMovie(String adminUserId, String imdbId, Movie request) {
        userService.requireAdmin(adminUserId);
        Movie movie = getMovie(imdbId);
        movie.setTitle(request.getTitle());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setTrailerLink(request.getTrailerLink());
        movie.setMovieLink(request.getMovieLink());
        movie.setPoster(request.getPoster());
        movie.setBanner(request.getBanner());
        movie.setRating(request.getRating());
        movie.setGenres(request.getGenres());
        movie.setBackdrops(request.getBackdrops());
        return movieRepository.save(movie);
    }

    public void deleteMovie(String adminUserId, String imdbId) {
        userService.requireAdmin(adminUserId);
        if (!movieRepository.existsById(imdbId)) {
            throw new IllegalArgumentException("Movie not found");
        }
        movieRepository.deleteById(imdbId);
    }
}
