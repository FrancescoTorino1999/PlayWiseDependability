package com.games.games_project.repositories;

import com.games.games_project.dto.FilterValuesDto;
import com.games.games_project.dto.GameSearchFiltersDto;
import com.games.games_project.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GameRepositoryCustom {
    FilterValuesDto getAllFilterValues();

    Page<Game> findGamesByFilters(Pageable pageable, GameSearchFiltersDto filters);

}
