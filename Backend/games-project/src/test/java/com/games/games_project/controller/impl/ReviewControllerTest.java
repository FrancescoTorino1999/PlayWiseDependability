package com.games.games_project.controller.impl;

import com.games.games_project.controller.ReviewController;
import com.games.games_project.dto.*;
import com.games.games_project.model.Review;
import com.games.games_project.model.User;
import com.games.games_project.service.GameService;
import com.games.games_project.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private GameService gameService;

    @BeforeEach
    void setup() {
        Mockito.reset(reviewService, gameService);
    }

    @Test
    @DisplayName("GET /reviews/games/{gameId}/reviews → restituisce pagina recensioni per un gioco")
    void testGetReviewsForGame() throws Exception {
        ReviewDetailsDto r1 = new ReviewDetailsDto();
        r1.setAuthor("User1");
        r1.setText("Ottimo gioco");
        ReviewDetailsDto r2 = new ReviewDetailsDto();
        r2.setAuthor("User2");
        r2.setText("Molto bello");

        PagedReviewsResponseDto<ReviewDetailsDto> page = new PagedReviewsResponseDto<>();
        page.setContent(List.of(r1, r2));
        page.setPage(0);
        page.setSize(5);
        page.setTotalPages(1);
        page.setTotalElements(2L);
        page.setFirst(true);
        page.setLast(true);

        when(reviewService.getReviewsByGameId(eq("6807a1905d04121deaab7d99"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/reviews/games/6807a1905d04121deaab7d99/reviews")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "date,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].author").value("User1"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("POST /reviews/games/reviewsByAuthor → restituisce recensioni utente")
    void testGetReviewsForUser() throws Exception {
        UserProfileReviewDto dto = new UserProfileReviewDto();
        dto.setAuthor("NuttyMan");
        dto.setText("Recensione positiva");
        dto.setGameTitle("Baldur's Gate 3");

        PagedReviewsResponseDto<UserProfileReviewDto> response =
                new PagedReviewsResponseDto<>(List.of(dto), 0, 5, 1, 1L, true, true);

        when(reviewService.getReviewsByUsername(eq("NuttyMan"), any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(post("/reviews/games/reviewsByAuthor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"NuttyMan\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].author").value("NuttyMan"))
                .andExpect(jsonPath("$.content[0].gameTitle").value("Baldur's Gate 3"));
    }

    @Test
    @DisplayName("POST /reviews/addReview → aggiunge una recensione con successo")
    void testAddReview() throws Exception {
        when(reviewService.addReview(any(Review.class))).thenReturn(true);

        mockMvc.perform(post("/reviews/addReview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"NuttyMan\",\"text\":\"Bellissimo titolo!\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /reviews/modifyReview → modifica recensione con successo")
    void testModifyReview() throws Exception {
        when(reviewService.modifyReview(any(Review.class))).thenReturn(true);

        mockMvc.perform(post("/reviews/modifyReview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"r1\",\"author\":\"NuttyMan\",\"text\":\"Aggiornata\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /reviews/deleteReview → elimina recensione con successo")
    void testDeleteReview() throws Exception {
        when(reviewService.deleteReview(any(Review.class))).thenReturn(true);

        mockMvc.perform(post("/reviews/deleteReview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"r1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /reviews/game/{gameId}/review → restituisce recensione per autore")
    void testGetGameReviewByAuthor_Found() throws Exception {
        ReviewDetailsDto dto = new ReviewDetailsDto();
        dto.setAuthor("NuttyMan");
        dto.setText("Fantastico titolo");
        dto.setScore(9);

        when(reviewService.getGameReviewByAuthor("6807a1905d04121deaab7d99", "NuttyMan"))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(get("/reviews/game/6807a1905d04121deaab7d99/review")
                        .param("author", "NuttyMan")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("NuttyMan"))
                .andExpect(jsonPath("$.score").value(9.0));
    }

    @Test
    @DisplayName("GET /reviews/game/{gameId}/review → nessuna recensione trovata")
    void testGetGameReviewByAuthor_NotFound() throws Exception {
        when(reviewService.getGameReviewByAuthor("6807a1905d04121deaab7d99", "UserX"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/reviews/game/6807a1905d04121deaab7d99/review")
                        .param("author", "UserX")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("GET /reviews/games/{gameId}/reviews → gestisce sort ascendente")
    void testGetReviewsForGame_SortAsc() throws Exception {
        ReviewDetailsDto r = new ReviewDetailsDto();
        r.setAuthor("Alpha");
        r.setText("Recensione A");

        PagedReviewsResponseDto<ReviewDetailsDto> page = new PagedReviewsResponseDto<>(List.of(r), 0, 5, 1, 1L, true, true);

        when(reviewService.getReviewsByGameId(eq("1"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/reviews/games/1/reviews")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "date,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].author").value("Alpha"));
    }

    @Test
    @DisplayName("GET /reviews/games/{gameId}/reviews → gestisce sort senza direzione (default DESC)")
    void testGetReviewsForGame_NoDirection_DefaultDesc() throws Exception {
        ReviewDetailsDto r = new ReviewDetailsDto();
        r.setAuthor("UserX");
        r.setText("Recensione X");

        PagedReviewsResponseDto<ReviewDetailsDto> page =
                new PagedReviewsResponseDto<>(List.of(r), 0, 5, 1, 1L, true, true);
        when(reviewService.getReviewsByGameId(eq("1"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/reviews/games/1/reviews")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "date")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].author").value("UserX"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(reviewService).getReviewsByGameId(eq("1"), captor.capture());
        Pageable pageable = captor.getValue();
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("date").getDirection());
    }

    @Test
    @DisplayName("GET /reviews/games/{gameId}/reviews → gestisce sort con 'ASC' maiuscolo")
    void testGetReviewsForGame_SortAscUppercase() throws Exception {
        ReviewDetailsDto r = new ReviewDetailsDto();
        r.setAuthor("Upper");
        r.setText("Maiuscolo ASC");

        PagedReviewsResponseDto<ReviewDetailsDto> page =
                new PagedReviewsResponseDto<>(List.of(r), 0, 5, 1, 1L, true, true);
        when(reviewService.getReviewsByGameId(eq("2"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/reviews/games/2/reviews")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "date,ASC")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].author").value("Upper"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(reviewService).getReviewsByGameId(eq("2"), captor.capture());
        Pageable pageable = captor.getValue();
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("date").getDirection());
    }

    @Test
    @DisplayName("GET /reviews/games/{gameId}/reviews → verifica Sort.Direction ASC e DESC")
    void testGetReviewsForGame_VerifySortDirection() throws Exception {
        PagedReviewsResponseDto<ReviewDetailsDto> pageAsc = new PagedReviewsResponseDto<>();
        pageAsc.setContent(List.of(new ReviewDetailsDto()));
        when(reviewService.getReviewsByGameId(eq("1"), any(Pageable.class))).thenReturn(pageAsc);

        mockMvc.perform(get("/reviews/games/1/reviews")
                        .param("sort", "date,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> captorAsc = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(reviewService).getReviewsByGameId(eq("1"), captorAsc.capture());
        Pageable pageableAsc = captorAsc.getValue();
        assertEquals(Sort.Direction.ASC, pageableAsc.getSort().getOrderFor("date").getDirection());

        PagedReviewsResponseDto<ReviewDetailsDto> pageDesc = new PagedReviewsResponseDto<>();
        pageDesc.setContent(List.of(new ReviewDetailsDto()));
        when(reviewService.getReviewsByGameId(eq("2"), any(Pageable.class))).thenReturn(pageDesc);

        mockMvc.perform(get("/reviews/games/2/reviews")
                        .param("sort", "date,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> captorDesc = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(reviewService).getReviewsByGameId(eq("2"), captorDesc.capture());
        Pageable pageableDesc = captorDesc.getValue();
        assertEquals(Sort.Direction.DESC, pageableDesc.getSort().getOrderFor("date").getDirection());
    }
}
