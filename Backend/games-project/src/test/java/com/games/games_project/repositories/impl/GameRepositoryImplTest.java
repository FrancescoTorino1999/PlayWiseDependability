package com.games.games_project.repositories.impl;

import com.games.games_project.dto.FilterValuesDto;
import com.games.games_project.dto.GameSearchFiltersDto;
import com.games.games_project.model.Game;
import com.games.games_project.repositories.GameRepositoryImpl;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GameRepositoryImplTest {

    /*@Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private GameRepositoryImpl gameRepositoryImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getAllFilterValues → restituisce DTO popolato con valori aggregati")
    void testGetAllFilterValues() {
        Document doc = new Document();
        doc.put("ratings", List.of("M", "T"));
        doc.put("genres", List.of("Action", "RPG"));
        doc.put("developers", List.of(List.of("Rockstar", "Larian")));
        doc.put("publishers", List.of(List.of("EA")));
        doc.put("themes", List.of(List.of("Open World")));
        doc.put("platforms", List.of(List.of("PC", "PS5")));
        doc.put("minMetaScore", 60.0);
        doc.put("maxMetaScore", 98.0);
        doc.put("minUserScore", 6.5);
        doc.put("maxUserScore", 9.5);
        doc.put("minReleaseDate", new Date());
        doc.put("maxReleaseDate", new Date());

        AggregationResults<Document> aggregationResults = mock(AggregationResults.class);
        when(aggregationResults.getUniqueMappedResult()).thenReturn(doc);
        when(mongoTemplate.aggregate(any(), eq("games"), eq(Document.class)))
                .thenReturn(aggregationResults);

        FilterValuesDto result = gameRepositoryImpl.getAllFilterValues();

        assertNotNull(result);
        assertTrue(result.getRatings().contains("M"));
        assertTrue(result.getPlatforms().contains("PC"));
        assertNotNull(result.getMinReleaseDate());
        assertNotNull(result.getMaxReleaseDate());
        assertEquals(60.0, result.getMinMetaScore());
        assertEquals(98.0, result.getMaxMetaScore());
    }

    @Test
    @DisplayName("findGamesByFilters → genera query corretta con criteri multipli")
    void testFindGamesByFilters_WithFilters() {
        GameSearchFiltersDto filters = new GameSearchFiltersDto();
        filters.setRatings(List.of("M"));
        filters.setGenres(List.of("Action"));
        filters.setDevelopers(List.of("Rockstar"));
        filters.setPublishers(List.of("EA"));
        filters.setThemes(List.of("Open World"));
        filters.setPlatforms(List.of("PC"));
        filters.setFromMetaScore(70.0);
        filters.setToMetaScore(95.0);
        filters.setFromUserScore(6.0);
        filters.setToUserScore(9.0);
        filters.setFromReleaseDate(new Date(1000000000L));
        filters.setToReleaseDate(new Date(2000000000L));

        when(mongoTemplate.count(any(Query.class), eq(Game.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Game.class)))
                .thenReturn(List.of(new Game()));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Game> page = gameRepositoryImpl.findGamesByFilters(pageable, filters);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Game.class));
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Game.class));
    }

    @Test
    @DisplayName("findGamesByFilters → gestisce filtri null o vuoti senza errori")
    void testFindGamesByFilters_EmptyFilters() {
        GameSearchFiltersDto filters = new GameSearchFiltersDto();

        when(mongoTemplate.count(any(Query.class), eq(Game.class))).thenReturn(0L);
        when(mongoTemplate.find(any(Query.class), eq(Game.class))).thenReturn(Collections.emptyList());

        PageRequest pageable = PageRequest.of(0, 5);
        Page<Game> result = gameRepositoryImpl.findGamesByFilters(pageable, filters);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Game.class));
    }

    @Test
    @DisplayName("getAllFilterValues → ritorna DTO vuoto se aggregation restituisce null")
    void testGetAllFilterValues_NullAggregationResult() {
        AggregationResults<Document> aggregationResults = mock(AggregationResults.class);
        when(aggregationResults.getUniqueMappedResult()).thenReturn(null);
        when(mongoTemplate.aggregate(any(), eq("games"), eq(Document.class)))
                .thenReturn(aggregationResults);

        FilterValuesDto dto = gameRepositoryImpl.getAllFilterValues();

        assertNotNull(dto);
        assertNull(dto.getMinMetaScore());
        assertNull(dto.getMaxMetaScore());
        assertNull(dto.getRatings());
    }*/
}
