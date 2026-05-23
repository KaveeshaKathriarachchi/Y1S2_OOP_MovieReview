package com.movieplatform.api.repository;

import com.movieplatform.api.model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovieId(String movieId);
    List<Review> findByUserId(String userId);
    Review findByReviewIdAndUserId(Long reviewId, String userId);
}
