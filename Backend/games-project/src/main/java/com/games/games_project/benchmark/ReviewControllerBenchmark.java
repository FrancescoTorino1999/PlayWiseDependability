package com.games.games_project.benchmark;

import com.games.games_project.dto.PagedReviewsResponseDto;
import com.games.games_project.dto.ReviewDetailsDto;
import com.games.games_project.service.ReviewService;
import org.openjdk.jmh.annotations.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@BenchmarkMode(Mode.All)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(8)
public class ReviewControllerBenchmark {

    private ReviewService reviewService;
    private String gameId;
    private String sortParam;

    @Setup(Level.Trial)
    public void setup() {
        reviewService = mock(ReviewService.class);
        when(reviewService.getReviewsByGameId(anyString(), any(Pageable.class)))
                .thenReturn(new PagedReviewsResponseDto<>());

        gameId = "g123";
        sortParam = "date,desc";
    }

    @Benchmark
    public Sort benchmarkSortParsing() {
        String[] sortParts = sortParam.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, sortParts[0]);
    }

    @Benchmark
    public Pageable benchmarkPageRequestCreation() {
        Sort sortBy = benchmarkSortParsing();
        return PageRequest.of(Math.max(0, 0), 5, sortBy);
    }

    @Benchmark
    public PagedReviewsResponseDto<ReviewDetailsDto> benchmarkGetReviewsForGameCall() {
        Pageable pageable = benchmarkPageRequestCreation();
        return reviewService.getReviewsByGameId(gameId, pageable);
    }
}
