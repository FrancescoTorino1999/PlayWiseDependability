package com.games.games_project.service;

import com.games.games_project.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GameService {
    Optional<GameDetailsDto> getGameDetailsById(String id);
    PagedGamesResponseDto<GamePreviewDto> getGamesPaginated(Pageable pageable);
    List<GamePreviewDto> findSuggestion(String value);
    FilterValuesDto getAllFilterValues();
    PagedGamesResponseDto<GamePreviewDto> findFilteredGames(Pageable correctedPageable, GameSearchFiltersDto filter);
    List<PlatformCountDto> getGameCountByPlatform();
}
