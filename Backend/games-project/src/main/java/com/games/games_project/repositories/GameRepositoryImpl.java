package com.games.games_project.repositories;


import com.games.games_project.dto.FilterValuesDto;
import com.games.games_project.dto.GameSearchFiltersDto;
import com.games.games_project.model.Game;
import com.games.games_project.utils.RatingComparator;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GameRepositoryImpl implements GameRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public FilterValuesDto getAllFilterValues() {
        FilterValuesDto dto = new FilterValuesDto();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group()
                        .addToSet("rating").as("ratings")
                        .addToSet("genre").as("genres")
                        .addToSet("developers").as("developers")
                        .addToSet("publishers").as("publishers")
                        .addToSet("themes").as("themes")
                        .addToSet("platforms").as("platforms")
                        .min("releaseDate").as("minReleaseDate")
                        .max("releaseDate").as("maxReleaseDate")
                        .min("metaScore").as("minMetaScore")
                        .max("metaScore").as("maxMetaScore")
                        .min("userScore").as("minUserScore")
                        .max("userScore").as("maxUserScore")
        );

        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, "games", Document.class);
        Document doc = result.getUniqueMappedResult();

        if (doc != null) {
            Set<String> sortedRatings = new TreeSet<>(new RatingComparator());
            sortedRatings.addAll((List<String>) doc.get("ratings"));
            dto.setRatings(sortedRatings);

            dto.setGenres(new TreeSet<>((List<String>) doc.get("genres")));
            dto.setDevelopers(new TreeSet<>(flattenList((List<List<String>>) doc.get("developers"))));
            dto.setPublishers(new TreeSet<>(flattenList((List<List<String>>) doc.get("publishers"))));
            dto.setThemes(new TreeSet<>(flattenList((List<List<String>>) doc.get("themes"))));
            dto.setPlatforms(new TreeSet<>(flattenList((List<List<String>>) doc.get("platforms"))));

            // Ordinamento delle date come stringhe (ISO_LOCAL_DATE)
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            dto.setMinReleaseDate(formatDate(doc.getDate("minReleaseDate"), formatter));
            dto.setMaxReleaseDate(formatDate(doc.getDate("maxReleaseDate"), formatter));

            // Metascore e UserScore (valori numerici)
            dto.setMinMetaScore(doc.getDouble("minMetaScore"));
            dto.setMaxMetaScore(doc.getDouble("maxMetaScore"));
            dto.setMinUserScore(doc.getDouble("minUserScore"));
            dto.setMaxUserScore(doc.getDouble("maxUserScore"));
        }

        return dto;
    }

    @Override
    public Page<Game> findGamesByFilters(Pageable pageable, GameSearchFiltersDto filters) {
        Query query = new Query();

        if (filters.getFromReleaseDate() != null || filters.getToReleaseDate() != null ||
            filters.getFromMetaScore() != null || filters.getToMetaScore() != null ||
            filters.getFromUserScore() != null || filters.getToUserScore() != null) {

            List<Criteria> andCriteria = new ArrayList<>();
            if (filters.getFromReleaseDate() != null) {
                andCriteria.add(Criteria.where("releaseDate").gte(filters.getFromReleaseDate()));
            }
            if (filters.getToReleaseDate() != null) {
                andCriteria.add(Criteria.where("releaseDate").lte(filters.getToReleaseDate()));
            }
            if (filters.getFromMetaScore() != null) {
                andCriteria.add(Criteria.where("metaScore").gte(filters.getFromMetaScore()));
            }
            if (filters.getToMetaScore() != null) {
                andCriteria.add(Criteria.where("metaScore").lte(filters.getToMetaScore()));
            }
            if (filters.getFromUserScore() != null) {
                andCriteria.add(Criteria.where("userScore").gte(filters.getFromUserScore()));
            }
            if (filters.getToUserScore() != null) {
                andCriteria.add(Criteria.where("userScore").lte(filters.getToUserScore()));
            }
            if(!andCriteria.isEmpty()){
                query.addCriteria(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
            }
        }

        if(filters.getRatings() != null && !filters.getRatings().isEmpty()){
            query.addCriteria(Criteria.where("rating").in(filters.getRatings()));
        }
        if(filters.getGenres() != null && !filters.getGenres().isEmpty()){
            query.addCriteria(Criteria.where("genre").in(filters.getGenres()));
        }
        if(filters.getDevelopers() != null && !filters.getDevelopers().isEmpty()){
            query.addCriteria(Criteria.where("developers").in(filters.getDevelopers()));
        }
        if(filters.getPublishers() != null && !filters.getPublishers().isEmpty()){
            query.addCriteria(Criteria.where("publishers").in(filters.getPublishers()));
        }
        if(filters.getThemes() != null && !filters.getThemes().isEmpty()){
            query.addCriteria(Criteria.where("themes").in(filters.getThemes()));
        }
        if(filters.getPlatforms() != null && !filters.getPlatforms().isEmpty()){
            query.addCriteria(Criteria.where("platforms").in(filters.getPlatforms()));
        }

        // Conta il numero totale di elementi prima della paginazione
        long totalCount = mongoTemplate.count(query, Game.class);

        // Applica la paginazione
        query.with(pageable);

        // Esegue la query e converte il risultato
        List<Game> games = mongoTemplate.find(query, Game.class);

        return new PageImpl<>(games, pageable, totalCount);
    }

    private Set<String> flattenList(List<List<String>> nestedList) {
        return nestedList == null ? new HashSet<>() :
                nestedList.stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    private String formatDate(Date date, DateTimeFormatter formatter) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter);
    }
}
