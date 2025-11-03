package com.games.games_project.service.impl;

import com.games.games_project.dto.ReviewDetailsDto;
import com.games.games_project.dto.ReviewsMonthlyCountDto;
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
import org.springframework.data.domain.Pageable;

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
    @DisplayName("getReviewsByUsername - utente con recensioni su più giochi")
    void testGetReviewsByUsername_WithReviews() {
        String author = "PlayerZ";
        Pageable pageable = PageRequest.of(0, 5);

        Review r1 = new Review();
        r1.setId("rev1");
        r1.setAuthor(author);
        r1.setText("Bellissimo gameplay!");
        r1.setScore(9);
        r1.setDate(new java.util.Date());
        r1.setGameId(new ObjectId("671a9f4e9c7a4a321c7a9e11"));

        Review r2 = new Review();
        r2.setId("rev2");
        r2.setAuthor(author);
        r2.setText("Grafica pazzesca!");
        r2.setScore(8);
        r2.setDate(new java.util.Date());
        r2.setGameId(new ObjectId("671a9f4e9c7a4a321c7a9e12"));

        Page<Review> reviewPage = new PageImpl<>(List.of(r1, r2), pageable, 2);

        Game g1 = new Game();
        g1.setId("671a9f4e9c7a4a321c7a9e11");
        g1.setTitle("Horizon Zero Dawn");
        g1.setCover("hzd.jpg");

        Game g2 = new Game();
        g2.setId("671a9f4e9c7a4a321c7a9e12");
        g2.setTitle("God of War");
        g2.setCover("gow.jpg");

        when(reviewRepository.findByAuthor(eq(author), eq(pageable))).thenReturn(reviewPage);
        when(gameRepository.findById("671a9f4e9c7a4a321c7a9e11")).thenReturn(Optional.of(g1));
        when(gameRepository.findById("671a9f4e9c7a4a321c7a9e12")).thenReturn(Optional.of(g2));

        var result = reviewService.getReviewsByUsername(author, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("PlayerZ", result.getContent().get(0).getAuthor());
        assertEquals("Horizon Zero Dawn", result.getContent().get(0).getGameTitle());
        assertEquals("God of War", result.getContent().get(1).getGameTitle());
        assertTrue(result.isFirst());
        verify(reviewRepository).findByAuthor(author, pageable);
        verify(gameRepository, times(2)).findById(anyString());
    }

    @Test
    @DisplayName("getReviewsByUsername - utente senza recensioni restituisce pagina vuota")
    void testGetReviewsByUsername_NoReviews() {
        String author = "EmptyUser";
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(reviewRepository.findByAuthor(eq(author), eq(pageable))).thenReturn(emptyPage);

        var result = reviewService.getReviewsByUsername(author, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(reviewRepository).findByAuthor(author, pageable);
        verifyNoInteractions(gameRepository);
    }

    @Test
    @DisplayName("getReviewsByUsername - review con gioco non trovato non deve causare errore")
    void testGetReviewsByUsername_GameNotFound() {
        String author = "MysteryPlayer";
        Pageable pageable = PageRequest.of(0, 5);

        Review review = new Review();
        review.setId("revX");
        review.setAuthor(author);
        review.setText("Gioco misterioso!");
        review.setScore(7);
        review.setDate(new java.util.Date());
        review.setGameId(new ObjectId("671a9f4e9c7a4a321c7a9e99"));

        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        when(reviewRepository.findByAuthor(eq(author), eq(pageable))).thenReturn(reviewPage);
        when(gameRepository.findById("671a9f4e9c7a4a321c7a9e99")).thenReturn(Optional.empty());

        var result = reviewService.getReviewsByUsername(author, pageable);

        assertEquals(1, result.getContent().size());
        assertNull(result.getContent().get(0).getGameTitle());
        assertNull(result.getContent().get(0).getGameCover());
        verify(reviewRepository).findByAuthor(author, pageable);
        verify(gameRepository).findById("671a9f4e9c7a4a321c7a9e99");
    }

    @Test
    @DisplayName("modifyReview - aggiorna recensione esistente e ricalcola media correttamente")
    void testModifyReview_UpdateExistingReview() {
        String reviewId = "rev001";
        ObjectId gameId = new ObjectId("671a9f4e9c7a4a321c7a9e01");

        Review existing = new Review();
        existing.setId(reviewId);
        existing.setGameId(gameId);
        existing.setText("Recensione vecchia");
        existing.setScore(7);
        existing.setDate(new java.util.Date());

        Review modified = new Review();
        modified.setId(reviewId);
        modified.setGameId(gameId);
        modified.setText("Recensione aggiornata");
        modified.setScore(9);
        modified.setDate(new java.util.Date());

        Game game = new Game();
        game.setId(gameId.toHexString());
        game.setTitle("Elden Ring");
        game.setUserScore(8.0);
        game.setReviewCount(100);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existing));
        when(gameRepository.findById(gameId.toHexString())).thenReturn(Optional.of(game));

        Boolean result = reviewService.modifyReview(modified);

        assertTrue(result);
        verify(reviewRepository).save(argThat(r ->
                r.getText().equals("Recensione aggiornata") &&
                        r.getScore() == 9
        ));
        verify(gameRepository).save(argThat(g ->
                g.getUserScore() == Math.round(((8.0 * 100) - 7 + 9) / 100)
        ));
    }

    @Test
    @DisplayName("modifyReview - review non trovata restituisce FALSE")
    void testModifyReview_NotFound() {
        Review modified = new Review();
        modified.setId("revMissing");
        modified.setScore(8);

        when(reviewRepository.findById("revMissing")).thenReturn(Optional.empty());

        Boolean result = reviewService.modifyReview(modified);

        assertFalse(result);
        verify(reviewRepository, never()).save(any());
        verify(gameRepository, never()).save(any());
    }

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
    @DisplayName("addReview - nuova review aggiorna correttamente i punteggi")
    void testAddReview_Success() {
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
    @DisplayName("addReview - review già presente genera eccezione")
    void testAddReview_AlreadyExists() {
        Review review = new Review();
        review.setGameId(new ObjectId("6807a1905d04121deaab7daa"));
        review.setAuthor("RPGFan");
        review.setScore(9);

        when(reviewRepository.findByGameIdAndAuthor(any(ObjectId.class), anyString()))
                .thenReturn(Optional.of(new Review()));

        assertThrows(RuntimeException.class, () -> reviewService.addReview(review));
        verify(reviewRepository, never()).save(any());
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("addReview - gioco non trovato genera eccezione")
    void testAddReview_GameNotFound() {
        Review review = new Review();
        review.setGameId(new ObjectId("6807a1905d04121deaab7daa"));
        review.setAuthor("NewUser");
        review.setScore(8);

        when(reviewRepository.findByGameIdAndAuthor(any(ObjectId.class), anyString()))
                .thenReturn(Optional.empty());
        when(gameRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reviewService.addReview(review));
        verify(reviewRepository).save(any());
    }

    @Test
    @DisplayName("deleteReview - cancella recensione e aggiorna media")
    void testDeleteReview_Normal() {
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
    @DisplayName("deleteReview - review unica azzera punteggio e count")
    void testDeleteReview_SingleReview() {
        Review review = new Review();
        review.setId("revUnique");
        review.setScore(8);
        review.setGameId(new ObjectId("6807a1905d04121deaab7da7"));

        Game game = new Game();
        game.setId("6807a1905d04121deaab7da7");
        game.setTitle("Solo Game");
        game.setUserScore(8.0);
        game.setReviewCount(1);

        when(reviewRepository.findById("revUnique")).thenReturn(Optional.of(review));
        when(gameRepository.findById(anyString())).thenReturn(Optional.of(game));

        Boolean result = reviewService.deleteReview(review);

        assertTrue(result);
        assertEquals(0.0, game.getUserScore());
        assertEquals(0, game.getReviewCount());
        verify(reviewRepository).deleteById("revUnique");
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("deleteReview - review non trovata restituisce FALSE")
    void testDeleteReview_NotFound() {
        Review review = new Review();
        review.setId("revMissing");

        when(reviewRepository.findById("revMissing")).thenReturn(Optional.empty());

        Boolean result = reviewService.deleteReview(review);

        assertFalse(result);
        verify(reviewRepository, never()).deleteById(anyString());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("getGameReviewByAuthor - review presente")
    void testGetGameReviewByAuthor_Present() {
        String gameId = "6807a1905d04121deaab7db1";

        Review review = new Review();
        review.setId("rHalf");
        review.setGameId(new ObjectId(gameId));
        review.setAuthor("ScienceGuy");
        review.setScore(10);
        review.setText("Capolavoro FPS");

        when(reviewRepository.findByGameIdAndAuthor(any(ObjectId.class), eq("ScienceGuy")))
                .thenReturn(Optional.of(review));

        Optional<ReviewDetailsDto> result = reviewService.getGameReviewByAuthor(gameId, "ScienceGuy");

        assertTrue(result.isPresent());
        assertEquals("ScienceGuy", result.get().getAuthor());
        assertEquals(10, result.get().getScore());
    }

    @Test
    @DisplayName("getGameReviewByAuthor - review non trovata")
    void testGetGameReviewByAuthor_NotFound() {
        when(reviewRepository.findByGameIdAndAuthor(any(ObjectId.class), eq("CowboyJohn")))
                .thenReturn(Optional.empty());

        var result = reviewService.getGameReviewByAuthor("6807a1905d04121deaab7dc1", "CowboyJohn");
        assertTrue(result.isEmpty());
    }

}
