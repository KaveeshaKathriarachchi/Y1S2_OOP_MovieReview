package com.movieplatform.api.controller;

import com.movieplatform.api.model.Movie;
import com.movieplatform.api.service.MovieService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> allMovies() {
        return movieService.allMovies();
    }

    @GetMapping("/{imdbId}")
    public Movie getMovie(@PathVariable String imdbId) {
        return movieService.getMovie(imdbId);
    }

    @GetMapping("/genre/{genre}")
    public List<Movie> byGenre(@PathVariable String genre) {
        return movieService.byGenre(genre);
    }

    @GetMapping("/release-date/{releaseDate}")
    public List<Movie> byReleaseDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDate) {
        return movieService.byReleaseDate(releaseDate);
    }

    @GetMapping("/{imdbId}/watch/{userId}")
    public Movie watchMovie(@PathVariable String imdbId, @PathVariable String userId) {
        return movieService.movieForPaidViewer(imdbId, userId);
    }

    @PostMapping
    public Movie addMovie(@RequestHeader("X-Admin-User") String adminUserId, @RequestBody Movie movie) {
        return movieService.addMovie(adminUserId, movie);
    }

    @PutMapping("/{imdbId}")
    public Movie updateMovie(@RequestHeader("X-Admin-User") String adminUserId,
                             @PathVariable String imdbId,
                             @RequestBody Movie movie) {
        return movieService.updateMovie(adminUserId, imdbId, movie);
    }

    @DeleteMapping("/{imdbId}")
    public void deleteMovie(@RequestHeader("X-Admin-User") String adminUserId, @PathVariable String imdbId) {
        movieService.deleteMovie(adminUserId, imdbId);
    }
}
