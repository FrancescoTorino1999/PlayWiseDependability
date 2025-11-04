package com.games.games_project.utils;

import com.games.games_project.dto.ReviewRequestDto;
import com.games.games_project.dto.UserRequestDto;
import com.games.games_project.model.Review;
import com.games.games_project.model.User;

import java.util.Date;

public class ConverterDTO {
    public static Review convertToEntity(ReviewRequestDto dto) {
        Review review = new Review();
        if (dto.getId() != null) {
            review.setId(dto.getId());
        }
        review.setAuthor(dto.getAuthor());
        review.setText(dto.getText());
        review.setScore(dto.getScore());
        review.setDate(dto.getDate() != null ? dto.getDate() : new Date());
        if (dto.getGameId() != null) {
            review.setGameId(dto.getGameId());
        }
        if (dto.getUserId() != null) {
            review.setUserId(dto.getUserId());
        }
        review.setGameName(dto.getGameName());
        return review;
    }

    public static User convertToEntity(UserRequestDto dto) {
        User user = new User();
        if (dto.getId() != null) {
            user.setId(dto.getId());
        }
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setGender(dto.getGender());
        user.setRole(dto.getRole());
        user.setBirthDate(dto.getBirthDate() != null ? dto.getBirthDate() : new Date());
        return user;
    }
}
