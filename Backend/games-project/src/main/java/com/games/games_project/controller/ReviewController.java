package com.games.games_project.controller;

import com.games.games_project.dto.*;
import com.games.games_project.model.Review;
import com.games.games_project.model.User;
import com.games.games_project.service.GameService;
import com.games.games_project.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.games.games_project.utils.ConverterDTO.convertToEntity;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private GameService gameService;

    @GetMapping("/games/{gameId}/reviews")
    @CrossOrigin(origins = "*", methods = RequestMethod.GET)
    public PagedReviewsResponseDto<ReviewDetailsDto> getReviewsForGame(
            @PathVariable String gameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "date,desc") String sort
    ) {
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortBy = Sort.by(direction, sortParts[0]);
        Pageable pageable = PageRequest.of(Math.max(page, 0), size, sortBy);
        return reviewService.getReviewsByGameId(gameId, pageable);
    }

    @PostMapping("/games/reviewsByAuthor")
    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    public PagedReviewsResponseDto<UserProfileReviewDto> getReviewsForUser(
            @RequestBody UserRequestDto user,
            @PageableDefault(page = 1, size = 5) Pageable pageable
    ) {
        User user1 = convertToEntity(user);
        int correctedPage = Math.max(pageable.getPageNumber() - 1, 0);
        Pageable correctedPageable = PageRequest.of(correctedPage, pageable.getPageSize(), pageable.getSort());
        return reviewService.getReviewsByUsername(user1.getUsername(), correctedPageable);
    }

    @PostMapping("/addReview")
    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    public ResponseEntity<Boolean> addReview(@RequestBody ReviewRequestDto dto) {
        Review review = convertToEntity(dto);
        boolean result = reviewService.addReview(review);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/modifyReview")
    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    public ResponseEntity<Boolean> modifyReview(@RequestBody ReviewRequestDto dto) {
        Review review = convertToEntity(dto);
        boolean result = reviewService.modifyReview(review);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/deleteReview")
    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteReview(@RequestBody ReviewRequestDto dto) {
        Review review = convertToEntity(dto);
        boolean result = reviewService.deleteReview(review);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/game/{gameId}/review")
    @CrossOrigin(origins = "*", methods = RequestMethod.GET)
    public ReviewDetailsDto getGameReviewByAuthor(
            @PathVariable String gameId,
            @RequestParam String author
    ) {
        Optional<ReviewDetailsDto> review = reviewService.getGameReviewByAuthor(gameId, author);
        return review.orElse(null);
    }
}
