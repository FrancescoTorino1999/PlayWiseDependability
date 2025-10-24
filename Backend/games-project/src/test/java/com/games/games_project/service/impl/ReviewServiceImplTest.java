package com.games.games_project.service.impl;

import com.games.games_project.dto.ReviewDetailsDto;
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
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private GameRepository gameRepository;
    @InjectMocks private ReviewServiceImpl reviewService;

    @BeforeEach
    void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    @DisplayName("getReviewsByGameId - GTA V restituisce recensioni corrette")
    void testGetReviewsByGameId_GTA() {
        String gtaId = "6807a1905d04121deaab7da0";

        Review r1 = new Review();
        r1.setId("rev1");
        r1.setAuthor("PlayerOne");
        r1.setText("Incredibile open world!");
        r1.setScore(9);
        r1.setGameId(new ObjectId(gtaId));

        Review r2 = new Review();
        r2.setId("rev2");
        r2.setAuthor("GamerX");
        r2.setText("Una delle migliori esperienze Rockstar.");
        r2.setScore(10);
        r2.setGameId(new ObjectId(gtaId));

        Page<Review> page = new PageImpl<>(List.of(r1, r2));

        when(reviewRepository.findByGameId(any(ObjectId.class), any())).thenReturn(page);

        var result = reviewService.getReviewsByGameId(gtaId, PageRequest.of(0, 5));

        assertEquals(2, result.getContent().size());
        assertEquals("PlayerOne", result.getContent().get(0).getAuthor());
    }

    @Test
    @DisplayName("addReview - nuova review su Baldur’s Gate 3 aggiorna correttamente i punteggi")
    void testAddReview_BaldursGate3() {
        Review review = new Review();
        review.setGameId(new ObjectId("6807a1905d04121deaab7daa"));
        review.setAuthor("RPGFan");
        review.setScore(9);

        Game game = new Game();
        game.setId("6807a1905d04121deaab7daa");
        game.setTitle("Baldur's Gate 3");
        game.setUserScore(8.6);
        game.setReviewCount(533);

        when(reviewRepository.findByGameIdAndAuthor(any(ObjectId.class), anyString()))
                .thenReturn(Optional.empty());
        when(gameRepository.findById(anyString())).thenReturn(Optional.of(game));

        Boolean result = reviewService.addReview(review);

        assertTrue(result);
        verify(reviewRepository).save(any(Review.class));
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("deleteReview - cancella recensione da BioShock e aggiorna media")
    void testDeleteReview_BioShock() {
        Review review = new Review();
        review.setId("revBio");
        review.setScore(8);
        review.setGameId(new ObjectId("6807a1905d04121deaab7da6"));

        Game game = new Game();
        game.setId("6807a1905d04121deaab7da6");
        game.setTitle("BioShock");
        game.setUserScore(8.7);
        game.setReviewCount(293);

        when(reviewRepository.findById("revBio")).thenReturn(Optional.of(review));
        when(gameRepository.findById(anyString())).thenReturn(Optional.of(game));

        Boolean result = reviewService.deleteReview(review);

        assertTrue(result);
        verify(gameRepository).save(any(Game.class));
        verify(reviewRepository).deleteById("revBio");
    }

    @Test
    @DisplayName("getGameReviewByAuthor - review presente su Half-Life")
    void testGetGameReviewByAuthor_HalfLife() {
        String gameId = "6807a1905d04121deaab7db1";

        Review review = new Review();
        review.setId("rHalf");
        review.setGameId(new ObjectId(gameId));
        review.setAuthor("ScienceGuy");
        review.setScore(10);
        review.setText("Capolavoro FPS, insuperato.");

        when(reviewRepository.findByGameIdAndAuthor(any(ObjectId.class), eq("ScienceGuy")))
                .thenReturn(Optional.of(review));

        Optional<ReviewDetailsDto> result = reviewService.getGameReviewByAuthor(gameId, "ScienceGuy");

        assertTrue(result.isPresent());
        assertEquals("ScienceGuy", result.get().getAuthor());
        assertEquals(10, result.get().getScore());
    }

    @Test
    @DisplayName("getGameReviewByAuthor - review non trovata su Red Dead Redemption")
    void testGetGameReviewByAuthor_RedDead_NotFound() {
        when(reviewRepository.findByGameIdAndAuthor(any(ObjectId.class), eq("CowboyJohn")))
                .thenReturn(Optional.empty());

        var result = reviewService.getGameReviewByAuthor("6807a1905d04121deaab7dc1", "CowboyJohn");
        assertTrue(result.isEmpty());
    }
}
