package com.games.games_project.benchmark;

import com.games.games_project.dto.PagedReviewsResponseDto;
import com.games.games_project.dto.ReviewDetailsDto;
import com.games.games_project.dto.UserProfileReviewDto;
import com.games.games_project.dto.ReviewsMonthlyCountDto;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import com.games.games_project.service.impl.ReviewServiceImpl;
import org.bson.types.ObjectId;
import org.openjdk.jmh.annotations.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ReviewServiceBenchmark {

    private ReviewServiceImpl reviewService;
    private ReviewRepository reviewRepository;
    private GameRepository gameRepository;

    private Review sampleReview;
    private Game sampleGame;
    private PageImpl<Review> reviewPage;

    @Setup(Level.Trial)
    public void setup() {
        reviewRepository = mock(ReviewRepository.class);
        gameRepository = mock(GameRepository.class);
        reviewService = new ReviewServiceImpl(reviewRepository, gameRepository);

        sampleGame = new Game();
        sampleGame.setId("507f1f77bcf86cd799439011");
        sampleGame.setReviewCount(3);
        sampleGame.setUserScore(7.0);
        sampleGame.setTitle("Mock Game");

        sampleReview = new Review();
        sampleReview.setId("12345");
        sampleReview.setAuthor("Francesco");
        sampleReview.setText("Great game!");
        sampleReview.setScore(8);
        sampleReview.setGameId(new ObjectId(sampleGame.getId()));
        sampleReview.setDate(new Date());

        reviewPage = new PageImpl<>(List.of(sampleReview));
    }

    // ------------------- getReviewsByGameId -------------------
    @Benchmark
    public PagedReviewsResponseDto<ReviewDetailsDto> benchmarkGetReviewsByGameId() {
        when(reviewRepository.findByGameId(any(ObjectId.class), any())).thenReturn(reviewPage);
        return reviewService.getReviewsByGameId(sampleGame.getId(), PageRequest.of(0, 10));
    }

    // ------------------- addReview -------------------
    @Benchmark
    public Boolean benchmarkAddReview() {
        when(reviewRepository.findByGameIdAndAuthor(any(), any())).thenReturn(Optional.empty());
        when(gameRepository.findById(any())).thenReturn(Optional.of(sampleGame));
        return reviewService.addReview(sampleReview);
    }

    // ------------------- modifyReview -------------------
    @Benchmark
    public Boolean benchmarkModifyReview() {
        when(reviewRepository.findById(any())).thenReturn(Optional.of(sampleReview));
        when(gameRepository.findById(any())).thenReturn(Optional.of(sampleGame));
        return reviewService.modifyReview(sampleReview);
    }

    // ------------------- deleteReview -------------------
    @Benchmark
    public Boolean benchmarkDeleteReview() {
        when(reviewRepository.findById(any())).thenReturn(Optional.of(sampleReview));
        when(gameRepository.findById(any())).thenReturn(Optional.of(sampleGame));
        return reviewService.deleteReview(sampleReview);
    }

    // ------------------- getReviewsByUsername -------------------
    @Benchmark
    public PagedReviewsResponseDto<UserProfileReviewDto> benchmarkGetReviewsByUsername() {
        when(reviewRepository.findByAuthor(any(), any())).thenReturn(reviewPage);
        when(gameRepository.findById(any())).thenReturn(Optional.of(sampleGame));
        return reviewService.getReviewsByUsername("Francesco", PageRequest.of(0, 10));
    }

    // ------------------- getGameReviewByAuthor -------------------
    @Benchmark
    public Optional<ReviewDetailsDto> benchmarkGetGameReviewByAuthor() {
        when(reviewRepository.findByGameIdAndAuthor(any(), any())).thenReturn(Optional.of(sampleReview));
        return reviewService.getGameReviewByAuthor(sampleGame.getId(), "Francesco");
    }

    // ------------------- getMonthlyReviewCount -------------------
    @Benchmark
    public List<ReviewsMonthlyCountDto> benchmarkGetMonthlyReviewCount() {
        when(reviewRepository.countReviewsPerMonth()).thenReturn(Collections.emptyList());
        return reviewService.getMonthlyReviewCount();
    }
}
