package com.games.games_project.service.impl;

import com.games.games_project.dto.PagedReviewsResponseDto;
import com.games.games_project.dto.ReviewDetailsDto;
import com.games.games_project.dto.ReviewsMonthlyCountDto;
import com.games.games_project.dto.UserProfileReviewDto;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import com.games.games_project.service.ReviewService;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    private final GameRepository gameRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, GameRepository gameRepository) {
        this.reviewRepository = reviewRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public PagedReviewsResponseDto<ReviewDetailsDto> getReviewsByGameId(String gameId, Pageable pageable) {
        ObjectId gameObjectId = new ObjectId(gameId);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Page<Review> reviewPage = reviewRepository.findByGameId(gameObjectId, pageable);

        // Mappa le recensioni a ReviewDetailsDto
        List<ReviewDetailsDto> reviewDtos = reviewPage.getContent().stream().map(review -> {
            ReviewDetailsDto dto = new ReviewDetailsDto();
            dto.setId(review.getId());
            dto.setAuthor(review.getAuthor());
            dto.setText(review.getText());
            dto.setScore(review.getScore());
            dto.setGameId(review.getId());
            dto.setDate(review.getDate() != null ? df.format(review.getDate()) : null);
            return dto;
        }).collect(Collectors.toList());

        // Crea e restituisce un PagedReviewsResponseDto con le recensioni mappate
        return new PagedReviewsResponseDto<>(
                reviewDtos,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalPages(),
                reviewPage.getTotalElements(),
                reviewPage.isFirst(),
                reviewPage.isLast()
        );
    }

    @Override
    @Transactional
    public Boolean addReview(Review review) {
        // Controllo se l'autore ha già recensito questo gioco
        Optional<Review> existing = reviewRepository.findByGameIdAndAuthor(review.getGameId(), review.getAuthor());
        if (existing.isPresent()) {
            throw new RuntimeException("Review already exists");
        }

        //Salva la recensione
        reviewRepository.save(review);

        //Recupera le Info del Gioco
        ObjectId gameId = review.getGameId();
        Game game = gameRepository.findById(String.valueOf(gameId))
                .orElseThrow(() -> new RuntimeException("Game not found!"));

        //Aggiorna punteggio e numero recensioni
        int numReviews = game.getReviewCount();
        double currentAvg = game.getUserScore();
        double newAvg = Math.round(((currentAvg * numReviews) + review.getScore()) / (numReviews + 1));
        game.setUserScore(newAvg);
        game.setReviewCount(numReviews + 1);

        //Salva modifiche
        gameRepository.save(game);

        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public Boolean modifyReview(Review review) {
        Optional<Review> existingReview = reviewRepository.findById(review.getId());

        if (existingReview.isPresent()) {
            Review updatedReview = existingReview.get();
            double oldScore = existingReview.get().getScore();
            double newScore = review.getScore();

            //Aggiorna Recensione
            updatedReview.setText(review.getText());
            updatedReview.setScore(review.getScore());
            updatedReview.setDate(review.getDate());
            reviewRepository.save(updatedReview);

            //Trova Info del Gioco
            ObjectId gameId = updatedReview.getGameId();
            Game game = gameRepository.findById(String.valueOf(gameId))
                    .orElseThrow(() -> new RuntimeException("Game not found!"));

            //Aggiorna Score e numero Recensioni
            int numReviews = game.getReviewCount();
            double currentAvg = game.getUserScore();
            double newAvg = Math.round(((currentAvg * numReviews) - oldScore + newScore) / numReviews);
            game.setUserScore(newAvg);
            gameRepository.save(game);

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Override
    @Transactional
    public Boolean deleteReview(Review review) {
        // Recupera la review
        Optional<Review> reviewOpt = reviewRepository.findById(review.getId());
        if (reviewOpt.isPresent()) {
            Review reviewToDelete = reviewOpt.get();
            double scoreToDelete = reviewToDelete.getScore();

            // Trova info gioco associato
            ObjectId gameId = reviewToDelete.getGameId();
            Game game = gameRepository.findById(String.valueOf(gameId))
                    .orElseThrow(() -> new RuntimeException("Game not found!"));

            // Aggiorna il punteggio medio e il numero di recensioni
            int numReviews = game.getReviewCount();
            double currentAvg = game.getUserScore();

            if (numReviews <= 1) {
                game.setUserScore(0.0);
                game.setReviewCount(0);
            } else {
                double newAvg = Math.round(((currentAvg * numReviews) - scoreToDelete) / (numReviews - 1));
                game.setUserScore(newAvg);
                game.setReviewCount(numReviews - 1);
            }

            // Salva il gioco aggiornato
            gameRepository.save(game);

            // Cancella la review
            reviewRepository.deleteById(review.getId());
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public PagedReviewsResponseDto<UserProfileReviewDto> getReviewsByUsername(String author, Pageable pageable) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Page<Review> reviewPage = reviewRepository.findByAuthor(author, pageable);

        // Mappa le recensioni a UserProfileReviewDto
        List<UserProfileReviewDto> reviewDtos = reviewPage.getContent().stream().map(review -> {
            Optional<Game> reviewedGame = gameRepository.findById(String.valueOf(review.getGameId()));
            UserProfileReviewDto dto = new UserProfileReviewDto();
            dto.setAuthor(review.getAuthor());
            dto.setScore(review.getScore());
            dto.setId(review.getId());
            dto.setText(review.getText());
            dto.setDate(review.getDate() != null ? df.format(review.getDate()) : null);

            if(reviewedGame.isPresent()) {
                Game game = reviewedGame.get();
                dto.setGameId(game.getId());
                dto.setGameTitle(game.getTitle());
                dto.setGameCover(game.getCover());
            }

            return dto;
        }).collect(Collectors.toList());

        // Crea e restituisce un PagedReviewsResponseDto con le recensioni mappate
        return new PagedReviewsResponseDto<>(
                reviewDtos,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalPages(),
                reviewPage.getTotalElements(),
                reviewPage.isFirst(),
                reviewPage.isLast()
        );
    }

    @Override
    public Optional<ReviewDetailsDto> getGameReviewByAuthor(String gameId, String author) {
        Optional<Review> reviewOpt = reviewRepository.findByGameIdAndAuthor(new ObjectId(gameId), author);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            ReviewDetailsDto dto = new ReviewDetailsDto();
            dto.setAuthor(review.getAuthor());
            dto.setScore(review.getScore());
            dto.setId(review.getId());
            dto.setText(review.getText());
            dto.setDate(review.getDate() != null ? df.format(review.getDate()) : null);
            dto.setGameId(gameId);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<ReviewsMonthlyCountDto> getMonthlyReviewCount() {
        return reviewRepository.countReviewsPerMonth();
    }


}
