package com.games.games_project.utils;

import com.games.games_project.dto.GameRequestDto;
import com.games.games_project.dto.ReviewRequestDto;
import com.games.games_project.dto.UserRequestDto;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.model.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class ConverterDTOTest {

    // ===== REVIEW TESTS =====
    @Test
    void convertToEntity_ShouldMapAllFields_WhenDtoIsFullyPopulated() {
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setId("1");
        dto.setAuthor("Mario");
        dto.setText("Ottimo gioco");
        dto.setScore(5);
        dto.setDate(new Date());
        dto.setGameId(new ObjectId());
        dto.setUserId(new ObjectId());
        dto.setGameName("Cyberpunk");

        Review review = ConverterDTO.convertToEntity(dto);

        assertEquals(dto.getId(), review.getId());
        assertEquals(dto.getAuthor(), review.getAuthor());
        assertEquals(dto.getText(), review.getText());
        assertEquals(dto.getScore(), review.getScore());
        assertEquals(dto.getGameName(), review.getGameName());
        assertNotNull(review.getDate());
        assertEquals(dto.getGameId(), review.getGameId());
        assertEquals(dto.getUserId(), review.getUserId());
    }

    @Test
    void convertToEntity_ShouldAssignCurrentDate_WhenDateIsNull() {
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setAuthor("Luigi");
        dto.setDate(null);
        Review review = ConverterDTO.convertToEntity(dto);
        assertNotNull(review.getDate());
    }

    // ===== USER TESTS =====
    @Test
    void convertToEntity_ShouldMapAllUserFields() {
        UserRequestDto dto = new UserRequestDto();
        dto.setId("u1");
        dto.setUsername("francesco");
        dto.setEmail("fra@mail.com");
        dto.setPassword("123");
        dto.setName("Francesco");
        dto.setSurname("Torino");
        dto.setGender("M");
        dto.setRole("Admin");
        dto.setBirthDate(new Date());

        User user = ConverterDTO.convertToEntity(dto);

        assertEquals(dto.getId(), user.getId());
        assertEquals(dto.getUsername(), user.getUsername());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getPassword(), user.getPassword());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getSurname(), user.getSurname());
        assertEquals(dto.getGender(), user.getGender());
        assertEquals(dto.getRole(), user.getRole());
        assertNotNull(user.getBirthDate());
    }

    @Test
    void convertToEntity_ShouldAssignDefaultDate_WhenBirthDateIsNull() {
        UserRequestDto dto = new UserRequestDto();
        dto.setUsername("mario");
        dto.setBirthDate(null);

        User user = ConverterDTO.convertToEntity(dto);

        assertNotNull(user.getBirthDate());
    }

    // ===== GAME TESTS =====
    @Test
    void convertToEntity_ShouldMapAllGameFields() {
        GameRequestDto dto = new GameRequestDto();
        dto.setId("g1");
        dto.setTitle("Zelda");
        dto.setReleaseDate(new Date());
        dto.setRating("PEGI 18");
        dto.setGenre("Adventure");
        dto.setDevelopers(List.of("Nintendo"));
        dto.setPublishers(List.of("Nintendo"));
        dto.setThemes(List.of("Fantasy"));
        dto.setPlatforms(List.of("Switch"));
        dto.setMetaScore(92.5);
        dto.setMetaScoreCount(5000.0);
        dto.setDescription("An epic adventure");
        dto.setStoryline("Save the kingdom");
        dto.setSummary("Great game");
        dto.setCover("cover.jpg");
        dto.setVideo("trailer.mp4");
        dto.setUserScore(9.8);
        dto.setReviewCount(1000);
        dto.setScreenshots(List.of("img1", "img2"));

        Game game = ConverterDTO.convertToEntity(dto);

        assertEquals(dto.getId(), game.getId());
        assertEquals(dto.getTitle(), game.getTitle());
        assertEquals(dto.getRating(), game.getRating());
        assertEquals(dto.getGenre(), game.getGenre());
        assertEquals(dto.getDevelopers(), game.getDevelopers());
        assertEquals(dto.getPublishers(), game.getPublishers());
        assertEquals(dto.getThemes(), game.getThemes());
        assertEquals(dto.getPlatforms(), game.getPlatforms());
        assertEquals(dto.getMetaScore(), game.getMetaScore());
        assertEquals(dto.getMetaScoreCount(), game.getMetaScoreCount());
        assertEquals(dto.getDescription(), game.getDescription());
        assertEquals(dto.getStoryline(), game.getStoryline());
        assertEquals(dto.getSummary(), game.getSummary());
        assertEquals(dto.getCover(), game.getCover());
        assertEquals(dto.getVideo(), game.getVideo());
        assertEquals(dto.getUserScore(), game.getUserScore());
        assertEquals(dto.getReviewCount(), game.getReviewCount());
        assertEquals(dto.getScreenshots(), game.getScreenshots());
    }

    @Test
    void convertToEntity_ShouldAssignDefaultReleaseDate_WhenNull() {
        GameRequestDto dto = new GameRequestDto();
        dto.setTitle("Elden Ring");
        dto.setReleaseDate(null);

        Game game = ConverterDTO.convertToEntity(dto);

        assertNotNull(game.getReleaseDate());
    }
}
