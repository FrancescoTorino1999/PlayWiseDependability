package com.games.games_project.repositories;

import com.games.games_project.dto.ReviewsMonthlyCountDto;
import com.games.games_project.model.Review;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review, String> {
    Page<Review> findByGameId(ObjectId gameId, Pageable pageable);
    Page<Review> findByAuthor(String author, Pageable pageable);
    void deleteByAuthor(String author);
    Optional<Review> findByGameIdAndAuthor(ObjectId gameId, String author);

    @Aggregation(pipeline = {
            "{ $group: { " +
                    "_id: { year: { $year: '$date' }, month: { $month: '$date' } }, " +
                    "count: { $sum: 1 } } }",
            "{ $project: { _id: 0, year: '$_id.year', month: '$_id.month', count: 1 } }",
            "{ $sort: { year: 1, month: 1 } }"
    })
    List<ReviewsMonthlyCountDto> countReviewsPerMonth();
}
