package com.games.games_project.service;

import com.games.games_project.dto.PagedReviewsResponseDto;
import com.games.games_project.dto.ReviewDetailsDto;
import com.games.games_project.dto.ReviewsMonthlyCountDto;
import com.games.games_project.dto.UserProfileReviewDto;
import com.games.games_project.model.Review;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    PagedReviewsResponseDto<ReviewDetailsDto> getReviewsByGameId(String gameId, Pageable pageable);

    Boolean addReview(Review review);

    Boolean modifyReview(Review review);

    Boolean deleteReview(Review review);

    PagedReviewsResponseDto<UserProfileReviewDto> getReviewsByUsername(String author, Pageable correctedPageable);

    Optional<ReviewDetailsDto> getGameReviewByAuthor(String gameId, String author);

    List<ReviewsMonthlyCountDto> getMonthlyReviewCount();
}
