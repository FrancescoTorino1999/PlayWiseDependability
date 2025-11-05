package com.games.games_project.benchmark;

import com.games.games_project.dto.GamePreviewDto;
import com.games.games_project.dto.GameDetailsDto;
import com.games.games_project.dto.PagedGamesResponseDto;
import com.games.games_project.dto.PlatformCountDto;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import com.games.games_project.service.impl.GameServiceImpl;
import org.bson.types.ObjectId;
import org.openjdk.jmh.annotations.*;
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
public class GameServiceBenchmark {

    private GameServiceImpl gameService;
    private GameRepository gameRepository;
    private ReviewRepository reviewRepository;

    private Game sampleGame;
    private Review sampleReview;
    private GamePreviewDto samplePreview;
    private PageImpl<Game> gamePage;
    private PageImpl<Review> reviewPage;

    @Setup(Level.Trial)
    public void setup() {
        gameRepository = mock(GameRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        gameService = new GameServiceImpl(gameRepository, reviewRepository);

        sampleGame = new Game();
        sampleGame.setId("507f1f77bcf86cd799439011");
        sampleGame.setTitle("Mock Game");
        sampleGame.setMetaScore(85.0);
        sampleGame.setUserScore(8.5);
        sampleGame.setCover("mock_cover.png");
        sampleGame.setReviewCount(3);
        sampleGame.setReleaseDate(new Date());

        sampleReview = new Review();
        sampleReview.setId("rev1");
        sampleReview.setAuthor("Francesco");
        sampleReview.setText("Excellent game!");
        sampleReview.setScore(9);
        sampleReview.setDate(new Date());
        sampleReview.setGameId(new ObjectId(sampleGame.getId()));

        samplePreview = new GamePreviewDto("507f1f77bcf86cd799439011", "Mock Game");
        gamePage = new PageImpl<>(List.of(sampleGame));
        reviewPage = new PageImpl<>(List.of(sampleReview));
    }

    @Benchmark
    public Optional<GameDetailsDto> benchmarkGetGameDetailsById() {
        when(gameRepository.findById(anyString())).thenReturn(Optional.of(sampleGame));
        when(reviewRepository.findByGameId(any(ObjectId.class), any(Pageable.class))).thenReturn(reviewPage);
        return gameService.getGameDetailsById(sampleGame.getId());
    }

    @Benchmark
    public PagedGamesResponseDto<GamePreviewDto> benchmarkGetGamesPaginated() {
        when(gameRepository.findAll(any(Pageable.class))).thenReturn(gamePage);
        return gameService.getGamesPaginated(PageRequest.of(0, 10));
    }

    @Benchmark
    public List<GamePreviewDto> benchmarkFindSuggestion() {
        when(gameRepository.findSuggestions(anyString())).thenReturn(List.of(samplePreview));
        return gameService.findSuggestion("Mock");
    }

    @Benchmark
    public List<GamePreviewDto> benchmarkFindSuggestionEmpty() {
        return gameService.findSuggestion("");
    }

    @Benchmark
    public List<PlatformCountDto> benchmarkGetGameCountByPlatform() {
        when(gameRepository.countGamesByPlatform()).thenReturn(List.of(new PlatformCountDto("PC", 5L)));
        return gameService.getGameCountByPlatform();
    }
}
