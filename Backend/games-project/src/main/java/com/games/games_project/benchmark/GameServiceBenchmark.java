package com.games.games_project.benchmark;

import com.games.games_project.dto.GameDetailsDto;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import com.games.games_project.service.impl.GameServiceImpl;
import org.bson.types.ObjectId;
import org.openjdk.jmh.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@BenchmarkMode(Mode.All)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(8)
public class GameServiceBenchmark {

    private GameServiceImpl gameService;
    private GameRepository gameRepository;
    private ReviewRepository reviewRepository;
    private String gameId;

    private Game sampleGame;
    private Page<Review> reviewPage;

    @Setup(Level.Trial)
    public void setup() {
        gameRepository = mock(GameRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        gameService = new GameServiceImpl(gameRepository, reviewRepository);

        gameId = "6807a1905d04121deaab7dd5";

        sampleGame = new Game();
        sampleGame.setId(gameId);
        sampleGame.setTitle("Tom Clancy's Splinter Cell: Chaos Theory");

        List<Review> reviews = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Review r = new Review();
            r.setId("r" + i);
            r.setAuthor("User" + i);
            r.setText("Review " + i);
            reviews.add(r);
        }
        reviewPage = new PageImpl<>(reviews);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(sampleGame));
        when(reviewRepository.findByGameId(any(ObjectId.class), any(Pageable.class))).thenReturn(reviewPage);
    }

    @Benchmark
    public Optional<GameDetailsDto> benchmarkGetGameDetailsById_SplinterCell_WithMoreThan5Reviews() {
        return gameService.getGameDetailsById(gameId);
    }
}
