package com.games.games_project.controller;

import com.games.games_project.dto.GenderCountDto;
import com.games.games_project.dto.PlatformCountDto;
import com.games.games_project.dto.ReviewsMonthlyCountDto;
import com.games.games_project.service.GameService;
import com.games.games_project.service.ReviewService;
import com.games.games_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/stats")
public class AdminController {

    @Autowired
    private GameService gameService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;

    @GetMapping("/reviews-per-month")
    public ResponseEntity<List<ReviewsMonthlyCountDto>> getReviewsPerMonth() {
        List<ReviewsMonthlyCountDto> stats = reviewService.getMonthlyReviewCount();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users-by-gender")
    public ResponseEntity<List<GenderCountDto>> getUsersByGender() {
        List<GenderCountDto> stats = userService.getUserCountByGender();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/games-by-platform")
    public ResponseEntity<List<PlatformCountDto>> getGamesByPlatform() {
        List<PlatformCountDto> stats = gameService.getGameCountByPlatform();
        return ResponseEntity.ok(stats);
    }
}
