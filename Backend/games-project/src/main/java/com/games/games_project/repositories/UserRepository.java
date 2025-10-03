package com.games.games_project.repositories;

import com.games.games_project.dto.GenderCountDto;
import com.games.games_project.model.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmailAndUsername(String email, String username);
    Optional<User> findByUsername(String username);
    Optional<User> deleteByUsername(String username);

    @Aggregation(pipeline = {
            "{ $group: { _id: '$gender', count: { $sum: 1 } } }",
            "{ $project: { _id: 0, gender: '$_id', count: 1 } }"
    })
    List<GenderCountDto> countUsersByGender();
}
