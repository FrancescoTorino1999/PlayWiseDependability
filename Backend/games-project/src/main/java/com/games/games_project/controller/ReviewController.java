package com.games.games_project.controller;

import com.games.games_project.dto.PagedReviewsResponseDto;
import com.games.games_project.dto.ReviewDetailsDto;
import com.games.games_project.dto.UserProfileReviewDto;
import com.games.games_project.model.Review;
import com.games.games_project.model.User;
import com.games.games_project.service.GameService;
import com.games.games_project.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private GameService gameService;


    @GetMapping("/games/{gameId}/reviews")
    public PagedReviewsResponseDto<ReviewDetailsDto> getReviewsForGame(
            @PathVariable String gameId,
            @PageableDefault(page = 1, size = 5) Pageable pageable
    ) {
        int correctedPage = Math.max(pageable.getPageNumber() - 1, 0);
        Pageable correctedPageable = PageRequest.of(correctedPage, pageable.getPageSize(), pageable.getSort());
        return reviewService.getReviewsByGameId(gameId, correctedPageable);
    }

    @PostMapping("/games/reviewsByAuthor")
    public PagedReviewsResponseDto<UserProfileReviewDto> getReviewsForUser(
            @RequestBody User user,
            @PageableDefault(page = 1, size = 5) Pageable pageable
    ) {
        int correctedPage = Math.max(pageable.getPageNumber() - 1, 0);
        Pageable correctedPageable = PageRequest.of(correctedPage, pageable.getPageSize(), pageable.getSort());
        return reviewService.getReviewsByUsername(user.getUsername(), correctedPageable);
    }

    @PostMapping("/addReview")
    public ResponseEntity<Boolean> addReview(@RequestBody Review review) {

        Boolean response = reviewService.addReview(review);

        return  ResponseEntity.ok(Boolean.TRUE);

    }

    @PostMapping("/modifyReview")
    public ResponseEntity<Boolean> modifyReview(@RequestBody Review review) {

        Boolean response = reviewService.modifyReview(review);

        return  ResponseEntity.ok(Boolean.TRUE);

    }

    @PostMapping("/deleteReview")
    public ResponseEntity<Boolean> deleteReview(@RequestBody Review review) {

        Boolean response = reviewService.deleteReview(review);

        return  ResponseEntity.ok(Boolean.TRUE);

    }

    @GetMapping("game/{gameId}/review")
    public ReviewDetailsDto getGameReviewByAuthor(
            @PathVariable String gameId,
            @RequestParam String author) {
        Optional<ReviewDetailsDto> review = reviewService.getGameReviewByAuthor(gameId, author);
        return review.orElse(null);
    }
}
