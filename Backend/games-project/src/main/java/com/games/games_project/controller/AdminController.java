package com.games.games_project.controller;

import com.games.games_project.dto.GenderCountDto;
import com.games.games_project.dto.PlatformCountDto;
import com.games.games_project.dto.ReviewsMonthlyCountDto;
import com.games.games_project.service.GameService;
import com.games.games_project.service.ReviewService;
import com.games.games_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/stats")
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class AdminController {

    @Autowired
    private GameService gameService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;

    @GetMapping("/reviews-per-month")
    @CrossOrigin(origins = "*", methods = RequestMethod.GET)
    public ResponseEntity<List<ReviewsMonthlyCountDto>> getReviewsPerMonth() {
        List<ReviewsMonthlyCountDto> stats = reviewService.getMonthlyReviewCount();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users-by-gender")
    @CrossOrigin(origins = "*", methods = RequestMethod.GET)
    public ResponseEntity<List<GenderCountDto>> getUsersByGender() {
        List<GenderCountDto> stats = userService.getUserCountByGender();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/games-by-platform")
    @CrossOrigin(origins = "*", methods = RequestMethod.GET)
    public ResponseEntity<List<PlatformCountDto>> getGamesByPlatform() {
        List<PlatformCountDto> stats = gameService.getGameCountByPlatform();
        return ResponseEntity.ok(stats);
    }
}
