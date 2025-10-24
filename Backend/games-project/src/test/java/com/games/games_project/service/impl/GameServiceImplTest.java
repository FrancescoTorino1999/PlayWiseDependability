package com.games.games_project.service.impl;

import com.games.games_project.dto.GameDetailsDto;
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
        Review r3 = new Review(); r3.setId("rev3"); r3.setAuthor("PlayerX"); r3.setText("One of the best Rockstar titles.");
        Page<Review> reviewPage = new PageImpl<>(List.of(r1, r2, r3));

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
}
