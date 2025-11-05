package com.games.games_project.benchmark;

import com.games.games_project.dto.GamePreviewDto;
import com.games.games_project.dto.PlatformCountDto;
import com.games.games_project.model.Game;
import com.games.games_project.repositories.GameRepository;
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
public class GameRepositoryBenchmark {

    private GameRepository gameRepository;
    private Game sampleGame;
    private Page<Game> gamePage;
    private List<GamePreviewDto> previewList;
    private List<PlatformCountDto> platformCountList;
    private Pageable pageable;

    @Setup(Level.Trial)
    public void setup() {
        gameRepository = mock(GameRepository.class);
        pageable = PageRequest.of(0, 10);

        sampleGame = new Game();
        sampleGame.setId("g1");
        sampleGame.setTitle("Mock Game");
        sampleGame.setGenre("Action");
        sampleGame.setPlatforms(List.of("PC", "PS5"));
        sampleGame.setMetaScore(85.0);
        sampleGame.setUserScore(8.2);

        gamePage = new PageImpl<>(List.of(sampleGame));
        previewList = List.of(new GamePreviewDto("g1", "Mock Game"));
        platformCountList = List.of(new PlatformCountDto("PC", 15L), new PlatformCountDto("PS5", 10L));
    }

    @Benchmark
    public Page<Game> benchmarkFindAll() {
        when(gameRepository.findAll(pageable)).thenReturn(gamePage);
        return gameRepository.findAll(pageable);
    }

    @Benchmark
    public List<GamePreviewDto> benchmarkFindSuggestions() {
        when(gameRepository.findSuggestions("Mock")).thenReturn(previewList);
        return gameRepository.findSuggestions("Mock");
    }

    @Benchmark
    public List<PlatformCountDto> benchmarkCountGamesByPlatform() {
        when(gameRepository.countGamesByPlatform()).thenReturn(platformCountList);
        return gameRepository.countGamesByPlatform();
    }
}
