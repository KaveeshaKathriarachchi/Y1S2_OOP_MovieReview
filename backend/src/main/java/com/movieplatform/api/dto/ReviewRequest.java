package com.movieplatform.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ReviewRequest {
    @NotBlank
    private String movieId;
    @Min(1)
    @Max(5)
    private int rating;
    @NotBlank
    private String reviewComment;

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
}
