package com.games.games_project.controller;

import com.games.games_project.dto.*;
import com.games.games_project.service.GameService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/games/{id}")
    public ResponseEntity<GameDetailsDto> getGameById(@PathVariable String id) {
        return gameService.getGameDetailsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/autocomplete/{query}")
    public ResponseEntity<List<GamePreviewDto>> findSuggestions(@PathVariable String query) {
        return ResponseEntity.ok(gameService.findSuggestion(query));
    }

    @GetMapping("/getFilters")
    public ResponseEntity<FilterValuesDto> getFilters() {
        return ResponseEntity.ok(gameService.getAllFilterValues());
    }

    @GetMapping("/games")
    public PagedGamesResponseDto<GamePreviewDto> getGamesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size,
            @RequestParam(defaultValue = "userScore,desc") String sort
    ) {
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortBy = Sort.by(direction, sortParts[0]);

        Pageable pageable = PageRequest.of(page, size, sortBy);
        return gameService.getGamesPaginated(pageable);
    }



    @PostMapping("/findFilteredGames")
    public PagedGamesResponseDto<GamePreviewDto> findFilteredGames(@PageableDefault(page = 1, size = 16) Pageable pageable, @RequestBody GameSearchFiltersDto filter) {
        int correctedPage = Math.max(pageable.getPageNumber() - 1, 0);
        Pageable correctedPageable = PageRequest.of(correctedPage, pageable.getPageSize(), pageable.getSort());
        return gameService.findFilteredGames(correctedPageable, filter);
    }
}
