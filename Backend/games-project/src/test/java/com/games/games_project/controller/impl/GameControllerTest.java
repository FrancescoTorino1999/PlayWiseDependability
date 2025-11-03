package com.games.games_project.controller.impl;

import com.games.games_project.controller.GameController;
import com.games.games_project.dto.*;
import com.games.games_project.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @BeforeEach
    void setup() {
        Mockito.reset(gameService);
    }

    @Test
    @DisplayName("GET /games/games/{id} → Gioco trovato (GTA IV)")
    void testGetGameById_Found() throws Exception {
        GameDetailsDto dto = new GameDetailsDto();
        dto.setId("6807a1905d04121deaab7d99");
        dto.setTitle("Grand Theft Auto IV");
        dto.setGenre("Open-World Action");
        dto.setUserScore(77.0);

        when(gameService.getGameDetailsById("6807a1905d04121deaab7d99"))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(get("/games/games/6807a1905d04121deaab7d99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Grand Theft Auto IV"))
                .andExpect(jsonPath("$.genre").value("Open-World Action"))
                .andExpect(jsonPath("$.userScore").value(77.0));
    }

    @Test
    @DisplayName("GET /games/games/{id} → Gioco non trovato")
    void testGetGameById_NotFound() throws Exception {
        when(gameService.getGameDetailsById("6807a1905d04121deaab7d99"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/games/games/6807a1905d04121deaab7d99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /games/autocomplete/{query} → restituisce suggerimenti")
    void testFindSuggestions() throws Exception {
        GamePreviewDto g1 = new GamePreviewDto();
        g1.setId("6807a1905d04121deaab7d99");
        g1.setTitle("Grand Theft Auto IV");
        g1.setCover("//images.igdb.com/igdb/image/upload/t_1080p/co2lbv.jpg");
        g1.setMetaScore(95.0);
        g1.setUserScore(8.7);

        GamePreviewDto g2 = new GamePreviewDto("6807a1905d04121deaab7da0", "Grand Theft Auto V");

        when(gameService.findSuggestion("Grand"))
                .thenReturn(List.of(g1, g2));

        mockMvc.perform(get("/games/autocomplete/Grand")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Grand Theft Auto IV"))
                .andExpect(jsonPath("$[1].title").value("Grand Theft Auto V"));
    }


    @Test
    @DisplayName("GET /games/games → restituisce pagina di giochi ordinata per userScore")
    void testGetGamesPage() throws Exception {
        GamePreviewDto g1 = new GamePreviewDto("6807a1905d04121deaab7d99", "GTA IV");
        GamePreviewDto g2 = new GamePreviewDto("6807a1905d04121deaab7da0", "GTA V");

        PagedGamesResponseDto<GamePreviewDto> page = new PagedGamesResponseDto<>();
        page.setContent(List.of(g1, g2));
        page.setPage(0);
        page.setSize(2);
        page.setTotalPages(1);
        page.setTotalElements(2L);
        page.setFirst(true);
        page.setLast(true);

        when(gameService.getGamesPaginated(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/games/games")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "userScore,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("GTA IV"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }


    @Test
    @DisplayName("GET /games/games → gestisce sort ascendente correttamente")
    void testGetGamesPage_SortAsc() throws Exception {
        GamePreviewDto g1 = new GamePreviewDto("1", "A Game");
        GamePreviewDto g2 = new GamePreviewDto("2", "B Game");

        PagedGamesResponseDto<GamePreviewDto> page = new PagedGamesResponseDto<>(
                List.of(g1, g2), 0, 2, 1, 2L, true, true);

        when(gameService.getGamesPaginated(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/games/games")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "title,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("A Game"));
    }
}
