package com.games.games_project.controller.impl;

import com.games.games_project.controller.AdminController;
import com.games.games_project.dto.GenderCountDto;
import com.games.games_project.dto.PlatformCountDto;
import com.games.games_project.dto.ReviewsMonthlyCountDto;
import com.games.games_project.service.GameService;
import com.games.games_project.service.ReviewService;
import com.games.games_project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;
    @MockBean
    private ReviewService reviewService;
    @MockBean
    private UserService userService;

    @BeforeEach
    void setup() {
        Mockito.reset(gameService, reviewService, userService);
    }

    @Test
    @DisplayName("GET /admin/stats/reviews-per-month → ritorna lista di recensioni per mese")
    void testGetReviewsPerMonth() throws Exception {
        ReviewsMonthlyCountDto dto1 = new ReviewsMonthlyCountDto(2024, 120L, 9);
        ReviewsMonthlyCountDto dto2 = new ReviewsMonthlyCountDto(2024, 150L, 10);
        when(reviewService.getMonthlyReviewCount()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/admin/stats/reviews-per-month")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].year").value(2024))
                .andExpect(jsonPath("$[0].month").value(9))
                .andExpect(jsonPath("$[0].count").value(120))
                .andExpect(jsonPath("$[1].month").value(10))
                .andExpect(jsonPath("$[1].count").value(150));
    }


    @Test
    @DisplayName("GET /admin/stats/users-by-gender → ritorna lista conteggi per genere")
    void testGetUsersByGender() throws Exception {
        GenderCountDto male = new GenderCountDto("M", 3L);
        GenderCountDto female = new GenderCountDto();
        female.setGender("F");
        female.setCount(5L);
        when(userService.getUserCountByGender()).thenReturn(List.of(male, female));

        mockMvc.perform(get("/admin/stats/users-by-gender")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gender").value("M"))
                .andExpect(jsonPath("$[1].count").value(5));
    }

    @Test
    @DisplayName("GET /admin/stats/games-by-platform → ritorna conteggio giochi per piattaforma")
    void testGetGamesByPlatform() throws Exception {
        PlatformCountDto pc = new PlatformCountDto("PC", 450L);
        PlatformCountDto ps5 = new PlatformCountDto("PlayStation 5", 320L);
        when(gameService.getGameCountByPlatform()).thenReturn(List.of(pc, ps5));

        mockMvc.perform(get("/admin/stats/games-by-platform")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].platform").value("PC"))
                .andExpect(jsonPath("$[1].count").value(320));
    }
}

