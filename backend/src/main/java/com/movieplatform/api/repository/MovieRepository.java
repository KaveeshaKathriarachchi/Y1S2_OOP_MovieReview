package com.movieplatform.api.repository;

import com.movieplatform.api.model.Movie;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, String> {
    Optional<Movie> findByTitle(String title);
    List<Movie> findByReleaseDate(LocalDate releaseDate);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE LOWER(g) = LOWER(:genre)")
    List<Movie> findByGenre(@Param("genre") String genre);
}
