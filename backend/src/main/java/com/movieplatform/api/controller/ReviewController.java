package com.movieplatform.api.controller;

import com.movieplatform.api.dto.ReviewRequest;
import com.movieplatform.api.model.Review;
import com.movieplatform.api.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{userId}")
    public Review create(@PathVariable String userId, @Valid @RequestBody ReviewRequest request) {
        return reviewService.create(userId, request);
    }

    @GetMapping("/movie/{movieId}")
    public List<Review> byMovie(@PathVariable String movieId) {
        return reviewService.byMovie(movieId);
    }

    @GetMapping("/user/{userId}")
    public List<Review> byUser(@PathVariable String userId) {
        return reviewService.byUser(userId);
    }

    @PutMapping("/{reviewId}/{userId}")
    public Review update(@PathVariable Long reviewId,
                         @PathVariable String userId,
                         @Valid @RequestBody ReviewRequest request) {
        return reviewService.update(reviewId, userId, request);
    }

    @DeleteMapping("/{reviewId}/{userId}")
    public void delete(@PathVariable Long reviewId, @PathVariable String userId) {
        reviewService.delete(reviewId, userId);
    }
}
