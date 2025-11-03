package com.games.games_project.service.impl;

import com.games.games_project.dto.*;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getGameDetailsById - ID nullo deve restituire Optional vuoto")
    void testGetGameDetailsById_NullId() {
        Optional<GameDetailsDto> result = gameService.getGameDetailsById(null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getGameDetailsById - ID inesistente restituisce Optional vuoto")
    void testGetGameDetailsById_GameNotFound() {
        String id = "6807a1905d04121deaab7d99"; // GTA IV
        when(gameRepository.findById(id)).thenReturn(Optional.empty());
        Optional<GameDetailsDto> result = gameService.getGameDetailsById(id);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getGameDetailsById - Splinter Cell trovato ma senza recensioni")
    void testGetGameDetailsById_SplinterCell_NoReviews() {
        String id = "6807a1905d04121deaab7dd5";
        Game game = new Game();
        game.setId(id);
        game.setTitle("Tom Clancy's Splinter Cell: Chaos Theory");
        game.setGenre("Action Adventure");
        game.setRating("M");
        game.setMetaScore(94.0);
        game.setUserScore(92.0);
        game.setReviewCount(33);
        game.setMetaScoreCount(1200.0);
        game.setDescription("Stealth action game.");
        game.setStoryline("Sam Fisher returns for a new mission.");
        game.setSummary("A top-tier stealth experience.");
        game.setCover("cover.jpg");
        game.setVideo("trailer.mp4");
        game.setDevelopers(List.of("Ubisoft Montreal"));
        game.setPublishers(List.of("Ubisoft"));
        game.setThemes(List.of("Stealth", "Espionage"));
        game.setPlatforms(List.of("PC", "Xbox"));
        game.setScreenshots(List.of("img1.jpg", "img2.jpg"));
        Date releaseDate = new Date();
        game.setReleaseDate(releaseDate);

        assertEquals(id, game.getId());
        assertEquals("Tom Clancy's Splinter Cell: Chaos Theory", game.getTitle());
        assertEquals("Action Adventure", game.getGenre());
        assertEquals("M", game.getRating());
        assertEquals(94.0, game.getMetaScore());
        assertEquals(92.0, game.getUserScore());
        assertEquals(33, game.getReviewCount());
        assertEquals(1200.0, game.getMetaScoreCount());
        assertEquals("Stealth action game.", game.getDescription());
        assertEquals("Sam Fisher returns for a new mission.", game.getStoryline());
        assertEquals("A top-tier stealth experience.", game.getSummary());
        assertEquals("cover.jpg", game.getCover());
        assertEquals("trailer.mp4", game.getVideo());
        assertEquals(List.of("Ubisoft Montreal"), game.getDevelopers());
        assertEquals(List.of("Ubisoft"), game.getPublishers());
        assertEquals(List.of("Stealth", "Espionage"), game.getThemes());
        assertEquals(List.of("PC", "Xbox"), game.getPlatforms());
        assertEquals(List.of("img1.jpg", "img2.jpg"), game.getScreenshots());
        assertEquals(releaseDate, game.getReleaseDate());

        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(reviewRepository.findByGameId(any(ObjectId.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Optional<GameDetailsDto> result = gameService.getGameDetailsById(id);

        assertTrue(result.isPresent());
        GameDetailsDto dto = result.get();
        assertEquals("Tom Clancy's Splinter Cell: Chaos Theory", dto.getTitle());
        assertEquals("Action Adventure", dto.getGenre());
        assertEquals(94, dto.getMetaScore());
        assertEquals(92.0, dto.getUserScore());
        assertNotNull(dto.getLatestReviews());
        assertEquals(0, dto.getLatestReviews().size());
    }

    @Test
    @DisplayName("getGameDetailsById - GTA IV trovato con 3 recensioni")
    void testGetGameDetailsById_GTAIV_With3Reviews() {
        String id = "6807a1905d04121deaab7d99";
        Game game = new Game();
        game.setId(id);
        game.setTitle("Grand Theft Auto IV");
        game.setGenre("Open-World Action");
        game.setMetaScore(98.5);
        game.setUserScore(77.0);
        game.setReviewCount(392);

        Review r1 = new Review(); r1.setId("rev1"); r1.setAuthor("NikoBellic"); r1.setText("Welcome to Liberty City!");
        Review r2 = new Review(); r2.setId("rev2"); r2.setAuthor("Roman"); r2.setText("Let's go bowling!");
        Review r3 = new Review();
        r3.setId("rev3");
        r3.setAuthor("PlayerX");
        r3.setText("One of the best Rockstar titles.");
        r3.setScore(9);
        Date now = new Date();
        r3.setDate(now);
        ObjectId gameId = new ObjectId("671a9f4e9c7a4a321c7a9e01");
        ObjectId userId = new ObjectId("671a9f4e9c7a4a321c7a9e02");
        r3.setGameId(gameId);
        r3.setUserId(userId);
        r3.setGameName("Grand Theft Auto IV");

        assertEquals("rev3", r3.getId());
        assertEquals("PlayerX", r3.getAuthor());
        assertEquals("One of the best Rockstar titles.", r3.getText());
        assertEquals(9, r3.getScore());
        assertEquals(now, r3.getDate());
        assertEquals(gameId, r3.getGameId());
        assertEquals(userId, r3.getUserId());
        assertEquals("Grand Theft Auto IV", r3.getGameName());        Page<Review> reviewPage = new PageImpl<>(List.of(r1, r2, r3));

        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(reviewRepository.findByGameId(any(ObjectId.class), any(Pageable.class))).thenReturn(reviewPage);

        Optional<GameDetailsDto> result = gameService.getGameDetailsById(id);

        assertTrue(result.isPresent());
        GameDetailsDto dto = result.get();
        assertEquals("Grand Theft Auto IV", dto.getTitle());
        assertEquals(3, dto.getLatestReviews().size());
        assertEquals("NikoBellic", dto.getLatestReviews().get(0).getAuthor());
    }

    @Test
    @DisplayName("getGameDetailsById - Splinter Cell con più di 5 recensioni")
    void testGetGameDetailsById_SplinterCell_WithMoreThan5Reviews() {
        String id = "6807a1905d04121deaab7dd5";
        Game game = new Game();
        game.setId(id);
        game.setTitle("Tom Clancy's Splinter Cell: Chaos Theory");

        List<Review> reviews = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Review r = new Review();
            r.setId("r" + i);
            r.setAuthor("User" + i);
            r.setText("Review " + i);
            reviews.add(r);
        }

        Page<Review> page = new PageImpl<>(reviews);

        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(reviewRepository.findByGameId(any(ObjectId.class), any(Pageable.class))).thenReturn(page);

        Optional<GameDetailsDto> result = gameService.getGameDetailsById(id);

        assertTrue(result.isPresent());
        assertEquals(8, result.get().getLatestReviews().size()); // repository mock returns 8
    }

    @Test
    @DisplayName("getGameDetailsById - GTA IV con data di rilascio nulla")
    void testGetGameDetailsById_GTAIV_ReleaseDateNull() {
        String id = "6807a1905d04121deaab7d99";
        Game game = new Game();
        game.setId(id);
        game.setTitle("Grand Theft Auto IV");
        game.setReleaseDate(null);

        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(reviewRepository.findByGameId(any(ObjectId.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Optional<GameDetailsDto> result = gameService.getGameDetailsById(id);

        assertTrue(result.isPresent());
        assertNull(result.get().getReleaseDate());
    }

    @Test
    @DisplayName("getGamesPaginated - deve restituire una pagina di anteprime giochi corretta")
    void testGetGamesPaginated() {
        Pageable pageable = mock(Pageable.class);

        Game g1 = new Game(); g1.setId("id1"); g1.setTitle("GTA IV"); g1.setCover("gta.jpg"); g1.setMetaScore(95.0); g1.setUserScore(8.7);
        Game g2 = new Game(); g2.setId("id2"); g2.setTitle("Splinter Cell"); g2.setCover("sc.jpg"); g2.setMetaScore(90.0); g2.setUserScore(9.1);
        List<Game> games = List.of(g1, g2);
        Page<Game> page = new PageImpl<>(games, pageable, 2);

        when(gameRepository.findAll(pageable)).thenReturn(page);

        var result = gameService.getGamesPaginated(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("GTA IV", result.getContent().get(0).getTitle());
        assertEquals("Splinter Cell", result.getContent().get(1).getTitle());
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(dto -> dto.getMetaScore() > 0));
        verify(gameRepository).findAll(pageable);
    }

    @Test
    @DisplayName("findSuggestion - valore nullo o vuoto restituisce lista vuota")
    void testFindSuggestion_NullOrEmpty() {
        assertTrue(gameService.findSuggestion(null).isEmpty());
        assertTrue(gameService.findSuggestion("   ").isEmpty());
        verifyNoInteractions(gameRepository);
    }

    @Test
    @DisplayName("findSuggestion - valore valido restituisce massimo 5 suggerimenti")
    void testFindSuggestion_ValidValue() {
        // Arrange
        List<GamePreviewDto> suggestions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            suggestions.add(new GamePreviewDto("id" + i, "Game " + i));
        }

        when(gameRepository.findSuggestions(anyString())).thenReturn(suggestions);

        // Act
        var result = gameService.findSuggestion("Game");

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size(), "Il risultato deve contenere massimo 5 elementi");
        assertEquals("Game 1", result.get(0).getTitle());
        assertEquals("id1", result.get(0).getId());
        verify(gameRepository, times(1)).findSuggestions("Game");
    }





    @Test
    @DisplayName("getGameCountByPlatform - deve restituire la lista delle piattaforme con conteggio giochi")
    void testGetGameCountByPlatform() {
        List<PlatformCountDto> counts = List.of(
                new PlatformCountDto("PC", 120L),
                new PlatformCountDto("PlayStation", 80L)
        );

        when(gameRepository.countGamesByPlatform()).thenReturn(counts);

        var result = gameService.getGameCountByPlatform();

        assertEquals(2, result.size());
        assertEquals("PC", result.get(0).getPlatform());
        assertEquals(120L, result.get(0).getCount());
        verify(gameRepository).countGamesByPlatform();
    }
}
