package com.games.games_project.service.impl;

import com.games.games_project.dto.*;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import com.games.games_project.service.GameService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GameServiceImpl implements GameService {


    @Autowired
    GameRepository gameRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Override
    public Optional<GameDetailsDto> getGameDetailsById(String id) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return gameRepository.findById(id).map(game -> {
            GameDetailsDto dto = new GameDetailsDto();
            dto.setId(game.getId());
            dto.setTitle(game.getTitle());
            dto.setReleaseDate(game.getReleaseDate() != null ? df.format(game.getReleaseDate()) : null);
            dto.setRating(game.getRating());
            dto.setGenre(game.getGenre());
            dto.setPublishers(game.getPublishers());
            dto.setDevelopers(game.getDevelopers());
            dto.setThemes(game.getThemes());
            dto.setPlatforms(game.getPlatforms());
            dto.setMetaScore(game.getMetaScore());
            dto.setMetaScoreCount(game.getMetaScoreCount());
            dto.setDescription(game.getDescription());
            dto.setStoryline(game.getStoryline());
            dto.setSummary(game.getSummary());
            dto.setCover(game.getCover());
            dto.setVideo(game.getVideo());
            dto.setUserScore(game.getUserScore());
            dto.setReviewCount(game.getReviewCount());
            dto.setScreenshots(game.getScreenshots());

            // Recupera le 5 recensioni più recenti
            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("date")));
            Page<Review> recentReviews = reviewRepository.findByGameId(new ObjectId(id), pageable);

            List<ReviewDetailsDto> reviewDtos = recentReviews.getContent().stream().map(review -> {
                ReviewDetailsDto reviewDto = new ReviewDetailsDto();
                reviewDto.setId(review.getId());
                reviewDto.setAuthor(review.getAuthor());
                reviewDto.setText(review.getText());
                reviewDto.setScore(review.getScore());
                reviewDto.setDate(review.getDate() != null ? df.format(review.getDate()) : null);
                return reviewDto;
            }).toList();
            dto.setLatestReviews(reviewDtos);

            return dto;
        });
    }

    @Override
    public PagedGamesResponseDto<GamePreviewDto> getGamesPaginated(Pageable pageable) {
        Page<Game> gamePage = gameRepository.findAll(pageable);

        List<GamePreviewDto> dtos = gamePage.getContent().stream().map(game -> {
            GamePreviewDto dto = new GamePreviewDto();
            dto.setId(game.getId());
            dto.setTitle(game.getTitle());
            dto.setCover(game.getCover());
            dto.setMetaScore(game.getMetaScore());
            dto.setUserScore(game.getUserScore());
            return dto;
        }).toList();

        return new PagedGamesResponseDto<>(
                dtos,
                gamePage.getNumber(),
                gamePage.getSize(),
                gamePage.getTotalPages(),
                gamePage.getTotalElements(),
                gamePage.isFirst(),
                gamePage.isLast()
        );
    }

    @Override
    public List<GamePreviewDto> findSuggestion(String value) {
        if (value == null || value.trim().isEmpty()) {
            return List.of();
        }
        return gameRepository.findSuggestions(value).stream()
                .limit(5) // Limita a 5 risultati
                .map(game -> new GamePreviewDto(game.getId(), game.getTitle()))
                .toList();
    }

    @Override
    public FilterValuesDto getAllFilterValues() {
        return gameRepository.getAllFilterValues();
    }

    @Override
    public PagedGamesResponseDto<GamePreviewDto> findFilteredGames(Pageable correctedPageable, GameSearchFiltersDto filter) {
        Page<Game> gamePage = gameRepository.findGamesByFilters(correctedPageable, filter);

        List<GamePreviewDto> dtos = gamePage.getContent().stream().map(game -> {
            GamePreviewDto dto = new GamePreviewDto();
            dto.setId(game.getId());
            dto.setTitle(game.getTitle());
            dto.setCover(game.getCover());
            dto.setMetaScore(game.getMetaScore());
            dto.setUserScore(game.getUserScore());
            return dto;
        }).toList();

        return new PagedGamesResponseDto<>(
                dtos,
                gamePage.getNumber(),
                gamePage.getSize(),
                gamePage.getTotalPages(),
                gamePage.getTotalElements(),
                gamePage.isFirst(),
                gamePage.isLast()
        );
    }

    @Override
    public List<PlatformCountDto> getGameCountByPlatform() {
        return gameRepository.countGamesByPlatform();
    }
}
