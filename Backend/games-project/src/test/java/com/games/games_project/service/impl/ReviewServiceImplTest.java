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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Calendar;
import java.util.Date;
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
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /* ------------------------------------------------------------
       getReviewsByUsername()
       ------------------------------------------------------------ */

    @Test
    @DisplayName("getReviewsByUsername - copertura completa della formattazione data")
    void testGetReviewsByUsername_DateFormattingBranches() {
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

        var first = result.getContent().get(0);
        var second = result.getContent().get(1);

        assertNotNull(first.getDate());
        assertNotNull(second.getDate());

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

    /* ------------------------------------------------------------
       modifyReview()
       ------------------------------------------------------------ */

    @Test
    @DisplayName("modifyReview - aggiorna recensione esistente e ricalcola media e data correttamente")
    void testModifyReview_UpdateExistingReview() {
        String reviewId = "rev001";
        ObjectId gameId = new ObjectId("671a9f4e9c7a4a321c7a9e01");

        Date oldDate = new Date(System.currentTimeMillis() - 100000);
        Date newDate = new Date(System.currentTimeMillis());

        Review existing = new Review();
        existing.setId(reviewId);
        existing.setGameId(gameId);
        existing.setText("Recensione vecchia");
        existing.setScore(6); // leggermente più basso
        existing.setDate(oldDate);

        Review modified = new Review();
        modified.setId(reviewId);
        modified.setGameId(gameId);
        modified.setText("Recensione aggiornata");
        modified.setScore(10); // leggermente più alto
        modified.setDate(newDate);

        Game game = new Game();
        game.setId(gameId.toHexString());
        game.setTitle("Elden Ring");
        game.setUserScore(8.3); // valore che non si arrotonda uguale
        game.setReviewCount(100);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existing));
        when(gameRepository.findById(gameId.toHexString())).thenReturn(Optional.of(game));

        Boolean result = reviewService.modifyReview(modified);
        assertTrue(result);

        double expectedNewAvg = Math.round(((8.3 * 100) - 6 + 10) / 100.0);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);

        verify(reviewRepository).save(reviewCaptor.capture());
        verify(gameRepository).save(gameCaptor.capture());

        Review savedReview = reviewCaptor.getValue();
        Game savedGame = gameCaptor.getValue();

        assertEquals("Recensione aggiornata", savedReview.getText());
        assertEquals(10, savedReview.getScore());
        assertEquals(newDate, savedReview.getDate());

        assertEquals(expectedNewAvg, savedGame.getUserScore(), 0.0001);
        assertEquals(100, savedGame.getReviewCount());
        assertEquals(gameId.toHexString(), savedGame.getId());

        assertNotEquals(8.3, savedGame.getUserScore());
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
    @DisplayName("modifyReview - modifica con punteggio inferiore aggiorna media correttamente")
    void testModifyReview_LowerScore() {
        Review existing = new Review();
        existing.setId("revLow");
        existing.setGameId(new ObjectId("671a9f4e9c7a4a321c7a9e77"));
        existing.setScore(9);

        Game game = new Game();
        game.setId(existing.getGameId().toHexString());
        game.setUserScore(8.0);
        game.setReviewCount(10);

        Review modified = new Review();
        modified.setId("revLow");
        modified.setScore(5);

        when(reviewRepository.findById("revLow")).thenReturn(Optional.of(existing));
        when(gameRepository.findById(anyString())).thenReturn(Optional.of(game));

        Boolean result = reviewService.modifyReview(modified);

        assertTrue(result);
        verify(gameRepository).save(argThat(g ->
                g.getUserScore() == Math.round(((8.0 * 10) - 9 + 5) / 10)
        ));
    }

    /* ------------------------------------------------------------
       getReviewsByGameId()
       ------------------------------------------------------------ */

    @Test
    @DisplayName("getReviewsByGameId - data e gameId corretti")
    void testGetReviewsByGameId_WithDateAndGameId() {
        String gtaId = "6807a1905d04121deaab7da0";
        ObjectId expectedObjectId = new ObjectId(gtaId);

        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.MARCH, 15, 10, 0, 0);
        Date reviewDate = cal.getTime();

        Review review = new Review();
        review.setId("rev1");
        review.setAuthor("PlayerOne");
        review.setText("Ottimo gioco!");
        review.setScore(9);
        review.setGameId(expectedObjectId);
        review.setDate(reviewDate);

        Page<Review> page = new PageImpl<>(List.of(review));
        when(reviewRepository.findByGameId(any(ObjectId.class), any(Pageable.class))).thenReturn(page);

        var result = reviewService.getReviewsByGameId(gtaId, PageRequest.of(0, 5));

        assertEquals(1, result.getContent().size());
        ReviewDetailsDto dto = result.getContent().get(0);

        assertNotNull(dto.getDate());
        assertEquals("2023-03-15", dto.getDate());

        assertEquals("rev1", dto.getGameId(), "Il campo gameId del DTO deve corrispondere all'id della review");

        assertEquals("rev1", dto.getId());
        assertEquals("PlayerOne", dto.getAuthor());
        assertEquals("Ottimo gioco!", dto.getText());
        assertEquals(9, dto.getScore());
    }




    /* ------------------------------------------------------------
       addReview()
       ------------------------------------------------------------ */

    @Test
    @DisplayName("addReview - uccide mutazioni matematiche su moltiplicazione e somma")
    void testAddReview_Success_MathSensitive() {
        Review review = new Review();
        review.setGameId(new ObjectId("6807a1905d04121deaab7daa"));
        review.setAuthor("RPGFan");
        review.setScore(8);

        Game game = new Game();
        game.setId("6807a1905d04121deaab7daa");
        game.setTitle("Baldur's Gate 3");
        game.setUserScore(6.0);
        game.setReviewCount(2);

        when(reviewRepository.findByGameIdAndAuthor(any(ObjectId.class), anyString()))
                .thenReturn(Optional.empty());
        when(gameRepository.findById(anyString())).thenReturn(Optional.of(game));

        Boolean result = reviewService.addReview(review);

        assertTrue(result);

        double expectedNewAvg = Math.round(((6.0 * 2) + 8.0) / (2 + 1)); // (12 + 8)/3 = 6.67 -> 7
        int expectedNewCount = 3;

        verify(gameRepository).save(argThat(g -> {
            assertEquals(expectedNewCount, g.getReviewCount());
            assertEquals(expectedNewAvg, g.getUserScore(), 0.0001);
            return true;
        }));

        verify(reviewRepository).save(argThat(r -> r.getAuthor().equals("RPGFan")));
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

    /* ------------------------------------------------------------
       deleteReview()
       ------------------------------------------------------------ */

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
        assertEquals(292, game.getReviewCount());

        assertTrue(result);
        verify(reviewRepository).deleteById("revBio");
        verify(gameRepository).save(argThat(g ->
                g.getUserScore() == Math.round(((8.7 * 293) - 8) / (293 - 1))
        ));
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

    /* ------------------------------------------------------------
       getGameReviewByAuthor()
       ------------------------------------------------------------ */

    @Test
    @DisplayName("getGameReviewByAuthor - review presente con data valorizzata")
    void testGetGameReviewByAuthor_Present() {
        String gameId = "6807a1905d04121deaab7db1";

        Review review = new Review();
        review.setId("rHalf");
        review.setGameId(new ObjectId(gameId));
        review.setAuthor("ScienceGuy");
        review.setScore(10);
        review.setText("Capolavoro FPS");
        review.setDate(java.sql.Date.valueOf("2024-11-06"));

        when(reviewRepository.findByGameIdAndAuthor(any(ObjectId.class), eq("ScienceGuy")))
                .thenReturn(Optional.of(review));

        Optional<ReviewDetailsDto> result = reviewService.getGameReviewByAuthor(gameId, "ScienceGuy");

        assertTrue(result.isPresent(), "La review deve essere presente");
        ReviewDetailsDto dto = result.get();

        assertEquals("rHalf", dto.getId());
        assertEquals("ScienceGuy", dto.getAuthor());
        assertEquals("Capolavoro FPS", dto.getText());
        assertEquals(10, dto.getScore());
        assertEquals("6807a1905d04121deaab7db1", dto.getGameId());

        assertNotNull(dto.getDate(), "La data deve essere valorizzata");
        assertEquals("2024-11-06", dto.getDate(), "La data deve essere formattata correttamente (yyyy-MM-dd)");
    }


    @Test
    @DisplayName("getGameReviewByAuthor - review non trovata")
    void testGetGameReviewByAuthor_NotFound() {
        when(reviewRepository.findByGameIdAndAuthor(any(ObjectId.class), eq("CowboyJohn")))
                .thenReturn(Optional.empty());

        var result = reviewService.getGameReviewByAuthor("6807a1905d04121deaab7dc1", "CowboyJohn");
        assertTrue(result.isEmpty());
    }


    /* ------------------------------------------------------------
       getMonthlyReviewCount()
       ------------------------------------------------------------ */

    @Test
    @DisplayName("getMonthlyReviewCount - restituisce lista corretta (year,count,month)")
    void testGetMonthlyReviewCount() {
        List<ReviewsMonthlyCountDto> mockCounts = List.of(
                new ReviewsMonthlyCountDto(2025, 12L, 1),
                new ReviewsMonthlyCountDto(2025, 18L, 2)
        );

        when(reviewRepository.countReviewsPerMonth()).thenReturn(mockCounts);

        List<ReviewsMonthlyCountDto> result = reviewService.getMonthlyReviewCount();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2025, result.get(0).getYear());
        assertEquals(12L, result.get(0).getCount());
        assertEquals(1, result.get(0).getMonth());
        assertEquals(18L, result.get(1).getCount());
        assertEquals(2, result.get(1).getMonth());

        verify(reviewRepository, times(1)).countReviewsPerMonth();
    }

    @Test
    @DisplayName("deleteReview - gioco con 0 recensioni non deve lanciare eccezioni")
    void testDeleteReview_NoReviews() {
        Review review = new Review();
        review.setId("revZero");
        review.setScore(7);
        review.setGameId(new ObjectId("6807a1905d04121deaab7dee"));

        Game game = new Game();
        game.setId("6807a1905d04121deaab7dee");
        game.setReviewCount(0);
        game.setUserScore(0.0);

        when(reviewRepository.findById("revZero")).thenReturn(Optional.of(review));
        when(gameRepository.findById(anyString())).thenReturn(Optional.of(game));

        Boolean result = reviewService.deleteReview(review);

        assertTrue(result);
        assertEquals(0, game.getReviewCount());
        assertEquals(0.0, game.getUserScore());
    }

    @Test
    @DisplayName("deleteReview - gioco con 2 recensioni aggiorna correttamente la media (uccide mutanti 132,136)")
    void testDeleteReview_TwoReviewsBoundary() {
        Review review = new Review();
        review.setId("revTwo");
        review.setScore(9);
        review.setGameId(new ObjectId("6807a1905d04121deaab7df0"));

        Game game = new Game();
        game.setId("6807a1905d04121deaab7df0");
        game.setUserScore(8.0);
        game.setReviewCount(2); // caso limite per boundary e media

        when(reviewRepository.findById("revTwo")).thenReturn(Optional.of(review));
        when(gameRepository.findById(anyString())).thenReturn(Optional.of(game));

        Boolean result = reviewService.deleteReview(review);

        assertTrue(result);
        verify(reviewRepository).deleteById("revTwo");
        verify(gameRepository).save(argThat(g -> {
            assertEquals(7.0, g.getUserScore(), 0.0001);
            assertEquals(1, g.getReviewCount());
            return true;
        }));
    }

    @Test
    @DisplayName("getReviewsByUsername - verifica completa dei campi del DTO (uccide mutanti 158–168)")
    void testGetReviewsByUsername_VerifyAllDtoFields() {
        String author = "MutantKiller";
        Pageable pageable = PageRequest.of(0, 5);

        Review review = new Review();
        review.setId("revKill");
        review.setAuthor(author);
        review.setText("Test completo!");
        review.setScore(10);
        review.setDate(java.sql.Date.valueOf("2025-11-07"));
        review.setGameId(new ObjectId("671a9f4e9c7a4a321c7a9e33"));

        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        Game game = new Game();
        game.setId("671a9f4e9c7a4a321c7a9e33");
        game.setTitle("Killer Game");
        game.setCover("cover.jpg");

        when(reviewRepository.findByAuthor(eq(author), eq(pageable))).thenReturn(reviewPage);
        when(gameRepository.findById("671a9f4e9c7a4a321c7a9e33")).thenReturn(Optional.of(game));

        var result = reviewService.getReviewsByUsername(author, pageable);

        assertEquals(1, result.getContent().size());
        var dto = result.getContent().get(0);

        assertEquals("revKill", dto.getId());
        assertEquals("MutantKiller", dto.getAuthor());
        assertEquals("Test completo!", dto.getText());
        assertEquals(10, dto.getScore());
        assertEquals("671a9f4e9c7a4a321c7a9e33", dto.getGameId());
        assertEquals("Killer Game", dto.getGameTitle());
        assertEquals("cover.jpg", dto.getGameCover());
        assertEquals("2025-11-07", dto.getDate());

        verify(reviewRepository).findByAuthor(author, pageable);
        verify(gameRepository).findById("671a9f4e9c7a4a321c7a9e33");
    }

    @Test
    @DisplayName("deleteReview - 1 sola recensione con media != punteggio: deve azzerare (uccide boundary 132)")
    void testDeleteReview_SingleReview_AsymmetricAverage() {
        ObjectId oid = new ObjectId("6807a1905d04121deaab7da7");

        Review review = new Review();
        review.setId("revUniqueSkew");
        review.setScore(8);
        review.setGameId(oid);

        Game game = new Game();
        game.setId(oid.toHexString());
        game.setTitle("Solo Game");
        game.setUserScore(7.9);
        game.setReviewCount(1);

        when(reviewRepository.findById("revUniqueSkew")).thenReturn(Optional.of(review));
        when(gameRepository.findById(eq(oid.toHexString()))).thenReturn(Optional.of(game));

        Boolean result = reviewService.deleteReview(review);

        assertTrue(result);
        assertEquals(0.0, game.getUserScore());
        assertEquals(0, game.getReviewCount());

        // e salvataggio effettuato
        verify(reviewRepository).deleteById("revUniqueSkew");
        verify(gameRepository).save(argThat(g -> {
            assertEquals(0.0, g.getUserScore());
            assertEquals(0, g.getReviewCount());
            return true;
        }));
    }

}
