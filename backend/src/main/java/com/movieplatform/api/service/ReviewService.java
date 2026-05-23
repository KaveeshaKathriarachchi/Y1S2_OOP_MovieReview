package com.movieplatform.api.service;

import com.movieplatform.api.dto.ReviewRequest;
import com.movieplatform.api.model.Review;
import com.movieplatform.api.repository.ReviewRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public ReviewService(ReviewRepository reviewRepository, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
    }

    public Review create(String userId, ReviewRequest request) {
        if (!userService.getUser(userId).isPaidUser()) {
            throw new IllegalArgumentException("Only paid users can write reviews");
        }
        Review review = new Review();
        review.setUserId(userId);
        review.setMovieId(request.getMovieId());
        review.setRating(request.getRating());
        review.setReviewComment(request.getReviewComment());
        review.setReviewDate(LocalDate.now());
        review.setLikesCount(0);
        review.setReviewStatus("active");
        return reviewRepository.save(review);
    }

    public List<Review> byMovie(String movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    public List<Review> byUser(String userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Review update(Long reviewId, String userId, ReviewRequest request) {
        if (!userService.getUser(userId).isPaidUser()) {
            throw new IllegalArgumentException("Only paid users can update reviews");
        }
        Review review = reviewRepository.findByReviewIdAndUserId(reviewId, userId);
        if (review == null) {
            throw new IllegalArgumentException("Review not found for this user");
        }
        review.setRating(request.getRating());
        review.setReviewComment(request.getReviewComment());
        return reviewRepository.save(review);
    }

    public void delete(Long reviewId, String userId) {
        if (!userService.getUser(userId).isPaidUser()) {
            throw new IllegalArgumentException("Only paid users can delete reviews");
        }
        Review review = reviewRepository.findByReviewIdAndUserId(reviewId, userId);
        if (review == null) {
            throw new IllegalArgumentException("Review not found for this user");
        }
        reviewRepository.delete(review);
    }
}
