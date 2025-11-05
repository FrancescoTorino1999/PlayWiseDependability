package com.games.games_project.benchmark;

import com.games.games_project.dto.ReviewsMonthlyCountDto;
import com.games.games_project.model.Review;
import com.games.games_project.repositories.ReviewRepository;
import org.bson.types.ObjectId;
import org.openjdk.jmh.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ReviewRepositoryBenchmark {

    private ReviewRepository reviewRepository;
    private Review sampleReview;
    private Page<Review> reviewPage;
    private Pageable pageable;
    private ObjectId gameId;
    private List<ReviewsMonthlyCountDto> monthlyCounts;

    @Setup(Level.Trial)
    public void setup() {
        reviewRepository = mock(ReviewRepository.class);
        pageable = PageRequest.of(0, 10);
        gameId = new ObjectId("6555abcd9876abcd1234abcd");

        sampleReview = new Review();
        sampleReview.setId("r1");
        sampleReview.setAuthor("Francesco");
        sampleReview.setText("Mock review");
        sampleReview.setScore(8);
        sampleReview.setDate(new Date());
        sampleReview.setGameId(gameId);

        reviewPage = new PageImpl<>(List.of(sampleReview));
        monthlyCounts = List.of(new ReviewsMonthlyCountDto(2025, 11, 5));
    }

    @Benchmark
    public Page<Review> benchmarkFindByGameId() {
        when(reviewRepository.findByGameId(gameId, pageable)).thenReturn(reviewPage);
        return reviewRepository.findByGameId(gameId, pageable);
    }

    @Benchmark
    public Page<Review> benchmarkFindByAuthor() {
        when(reviewRepository.findByAuthor("Francesco", pageable)).thenReturn(reviewPage);
        return reviewRepository.findByAuthor("Francesco", pageable);
    }

    @Benchmark
    public Optional<Review> benchmarkFindByGameIdAndAuthor() {
        when(reviewRepository.findByGameIdAndAuthor(gameId, "Francesco")).thenReturn(Optional.of(sampleReview));
        return reviewRepository.findByGameIdAndAuthor(gameId, "Francesco");
    }

    @Benchmark
    public void benchmarkDeleteByAuthor() {
        doNothing().when(reviewRepository).deleteByAuthor("Francesco");
        reviewRepository.deleteByAuthor("Francesco");
    }

    @Benchmark
    public List<ReviewsMonthlyCountDto> benchmarkCountReviewsPerMonth() {
        when(reviewRepository.countReviewsPerMonth()).thenReturn(monthlyCounts);
        return reviewRepository.countReviewsPerMonth();
    }
}
