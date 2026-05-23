package com.movieplatform.api.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @Column(length = 30)
    private String imdbId;

    @Column(nullable = false)
    private String title;

    private LocalDate releaseDate;

    @Column(length = 700)
    private String trailerLink;

    @Column(length = 700)
    private String movieLink;

    @Column(length = 700)
    private String poster;

    @Column(length = 700)
    private String banner;

    private Double rating;

    @ElementCollection
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_imdbId"))
    @Column(name = "genre")
    private List<String> genres = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "movie_backdrops", joinColumns = @JoinColumn(name = "movie_imdbId"))
    @Column(name = "backdrop_url", length = 700)
    private List<String> backdrops = new ArrayList<>();

    public String getImdbId() { return imdbId; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String getTrailerLink() { return trailerLink; }
    public void setTrailerLink(String trailerLink) { this.trailerLink = trailerLink; }
    public String getMovieLink() { return movieLink; }
    public void setMovieLink(String movieLink) { this.movieLink = movieLink; }
    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }
    public String getBanner() { return banner; }
    public void setBanner(String banner) { this.banner = banner; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }
    public List<String> getBackdrops() { return backdrops; }
    public void setBackdrops(List<String> backdrops) { this.backdrops = backdrops; }
}
