package com.games.games_project.service.impl;

import com.games.games_project.dto.GenderCountDto;
import com.games.games_project.dto.LoginRequestDto;
import com.games.games_project.dto.LoginResponseDto;
import com.games.games_project.dto.RegistrationRequestDto;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.model.User;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import com.games.games_project.repositories.UserRepository;
import com.games.games_project.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private GameRepository gameRepository;


    @Override
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        if(loginRequest==null || loginRequest.getUsername()==null || loginRequest.getPassword()==null){
            return null;
        }

        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        if(user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())){
            return new LoginResponseDto(user.get().getId(), user.get().getUsername(), user.get().getRole());
        }
        else{
            return null;
        }
    }

    @Override
    public Boolean register(RegistrationRequestDto registrationRequest) {
        if(registrationRequest==null || registrationRequest.getUsername()==null || registrationRequest.getPassword()==null || registrationRequest.getEmail()==null){
            return false;
        }

        if (userRepository.findByEmailAndUsername(registrationRequest.getEmail(), registrationRequest.getUsername()).isPresent()){
            return false;
        }

        User newUser = new User();
        newUser.setUsername(registrationRequest.getUsername());
        newUser.setPassword(registrationRequest.getPassword());
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setName(registrationRequest.getName());
        newUser.setSurname(registrationRequest.getSurname());
        newUser.setGender(registrationRequest.getGender());
        newUser.setBirthDate(registrationRequest.getBirthDate());
        newUser.setRole("USER");
        userRepository.save(newUser);

        return true;
    }

    @Override
    public User getUserInfo(String username) {
        if(!username.isEmpty()){
            return userRepository.findByUsername(username).get();
        }
        else {
            return new User();
        }
    }

    @Override
    public Boolean deleteUser(User user) {
        // Recupera tutte le review dell'utente
        Page<Review> reviewsPage = reviewRepository.findByAuthor(user.getUsername(), Pageable.unpaged());
        List<Review> reviews = reviewsPage.getContent();

        // Elimina Tutte le Review dell`utente e aggiorna i punteggi dei giochi
        for (Review review : reviews) {
            // Recupera il gioco associato alla review
            ObjectId gameId = review.getGameId();
            Game game = gameRepository.findById(String.valueOf(gameId))
                    .orElseThrow(() -> new RuntimeException("Game not Found!"));

            // Aggiorna Score e Numero Recensioni
            int numReviews = game.getReviewCount();
            double currentAvg = game.getUserScore();
            double reviewScore = review.getScore();

            if (numReviews <= 1) {
                game.setUserScore(0.0);
                game.setReviewCount(0);
            } else {
                double newAvg = Math.round(((currentAvg * numReviews) - reviewScore) / (numReviews - 1));
                game.setUserScore(newAvg);
                game.setReviewCount(numReviews - 1);
            }
            gameRepository.save(game);

            // Elimina la review
            reviewRepository.deleteById(review.getId());
        }

        // Elimina l'utente
        userRepository.deleteByUsername(user.getUsername());

        return Boolean.TRUE;
    }

    @Override
    public Boolean updateUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if(existingUser.isPresent()){
            User updatedUser = existingUser.get();
            if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
                updatedUser.setUsername(user.getUsername());
            }
            if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                updatedUser.setEmail(user.getEmail());
            }
            if (user.getName() != null && !user.getName().trim().isEmpty()) {
                updatedUser.setName(user.getName());
            }
            if (user.getSurname() != null && !user.getSurname().trim().isEmpty()) {
                updatedUser.setSurname(user.getSurname());
            }
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                updatedUser.setPassword(user.getPassword());
            }

            userRepository.save(updatedUser);
            return true;
        }
        else {
            return Boolean.FALSE;
        }
    }

    @Override
    public List<GenderCountDto> getUserCountByGender() {
        return userRepository.countUsersByGender();
    }
}
