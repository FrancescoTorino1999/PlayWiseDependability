package com.games.games_project.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.games.games_project.model.Game;
import com.games.games_project.model.Review;
import com.games.games_project.model.User;
import com.games.games_project.repositories.GameRepository;
import com.games.games_project.repositories.ReviewRepository;
import com.games.games_project.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class JsonDataImporter implements CommandLineRunner {
    private static final Logger LOGGER = Logger.getLogger(JsonDataImporter.class.getName());

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${import.data.folder}")
    private String importFolder;

    @Value("${import.data.enabled:false}")
    private boolean importEnabled;

    public JsonDataImporter(GameRepository gameRepository,
                            UserRepository userRepository,
                            ReviewRepository reviewRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!importEnabled) {
            LOGGER.info("Data Import Disabled...");
            return;
        }

        try {
            importGames();
            importUsers();
            importReviews();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during Data Import...", e);
        }
    }

    /**
     * Importa i dati dei giochi dal file JSON "BD2_Games.json".
     * Se la collezione dei giochi non è vuota l'importazione viene saltata.
     */
    private void importGames() {
        if (gameRepository.count() > 0) {
            LOGGER.info("Collection 'games' is not empty... Import Skipped!");
            return;
        }

        try {
            String path = importFolder + "BD2_Games.json";
            InputStream inputStream = new ClassPathResource(path).getInputStream();
            List<Game> games = objectMapper.readValue(inputStream, new TypeReference<List<Game>>() {});
            gameRepository.saveAll(games);
            LOGGER.info("Imported " + games.size() + " games from " + path);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during games import...", e);
        }
    }

    /**
     * Importa i dati degli utenti dal file JSON "BD2_Users.json".
     * Se la collezione degli utenti non è vuota l'importazione viene saltata.
     */
    private void importUsers() {
        if (userRepository.count() > 0) {
            LOGGER.info("Collection 'users' is not empty... Import Skipped!");
            return;
        }

        try {
            String path = importFolder + "BD2_Users.json";
            InputStream inputStream = new ClassPathResource(path).getInputStream();
            List<User> users = objectMapper.readValue(inputStream, new TypeReference<List<User>>() {});
            userRepository.saveAll(users);
            LOGGER.info("Imported " + users.size() + " users from " + path);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during users import...", e);
        }
    }

    /**
     * Importa le recensioni dal file JSON "BD2_Reviews.json".
     * Oltre ad associare il gioco (tramite il titolo), viene effettuato anche il mapping
     * per associare l'utente (tramite il campo "author" che corrisponde al "username" dell'utente).
     * Se il gioco o l’utente non vengono trovati, la recensione viene ignorata.
     */
    private void importReviews() {
        if (reviewRepository.count() > 0) {
            LOGGER.info("Collection 'reviews' is not empty... Import Skipped!");
            return;
        }

        try {
            // Precaricamento dei Giochi
            List<Game> allGames = gameRepository.findAll();
            Map<String, String> titleToIdMap = new HashMap<>();
            for (Game game : allGames) {
                titleToIdMap.put(game.getTitle().toLowerCase(), game.getId());
            }

            // Precaricamento degli Utenti
            List<User> allUsers = userRepository.findAll();
            Map<String, String> usernameToIdMap = new HashMap<>();
            for (User user : allUsers) {
                usernameToIdMap.put(user.getUsername().toLowerCase(), user.getId());
            }

            // Lettura delle Recensioni
            String path = importFolder + "BD2_Reviews.json";
            InputStream inputStream = new ClassPathResource(path).getInputStream();
            List<Map<String, Object>> rawReviews = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});

            List<Review> reviews = new ArrayList<>();

            for (Map<String, Object> raw : rawReviews) {
                String title = (String) raw.get("title");
                if (title == null)
                    continue;

                // Verifica l'associazione con il gioco
                String gameId = titleToIdMap.get(title.toLowerCase());
                if (gameId == null) {
                    LOGGER.warning("No Game found with title: " + title);
                    continue;
                }

                // Verifica l'associazione con l'utente (basata sul campo "author" che corrisponde al "username")
                String author = (String) raw.get("author");
                if (author == null)
                    continue;
                String userId = usernameToIdMap.get(author.toLowerCase());
                if (userId == null) {
                    LOGGER.warning("No User found with username: " + author);
                    continue;
                }

                // Creazione e popolamento della review
                Review review = new Review();
                review.setGameId(new ObjectId(gameId));
                review.setUserId(new ObjectId(userId)); // Associazione dell'utente tramite ID
                review.setAuthor(author);
                review.setText((String) raw.get("text"));

                Object scoreObj = raw.get("score");
                if (scoreObj instanceof Number) {
                    review.setScore(((Number) scoreObj).intValue());
                }

                Object dateObj = raw.get("date");
                if (dateObj instanceof String) {
                    review.setDate(objectMapper.convertValue(dateObj, Date.class));
                }

                reviews.add(review);
            }

            reviewRepository.saveAll(reviews);
            LOGGER.info("Imported " + reviews.size() + " reviews from " + path);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during reviews import...", e);
        }
    }
}
