package com.games.games_project.repositories;

import com.games.games_project.dto.GamePreviewDto;
import com.games.games_project.dto.PlatformCountDto;
import com.games.games_project.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface GameRepository extends MongoRepository<Game, String>, GameRepositoryCustom {
    Page<Game> findAll(Pageable pageable);

    @Query(value = "{ 'title': { $regex: ?0, $options: 'i' } }", fields = "{ 'id': 1, 'title': 1 }")
    List<GamePreviewDto> findSuggestions(@Param("query") String query);

    @Aggregation(pipeline = {
            "{ $unwind: '$platforms' }",
            "{ $group: { _id: '$platforms', count: { $sum: 1 } } }",
            "{ $project: { _id: 0, platform: '$_id', count: 1 } }",
            "{ $sort: { count: -1 } }"
    })
    List<PlatformCountDto> countGamesByPlatform();

}
