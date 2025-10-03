package com.games.games_project.stubs.impl;

import com.games.games_project.model.Game;
import com.games.games_project.stubs.ReviewClient;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReviewClientImpl implements ReviewClient {


    @Override
    public Game getGameById(String gameId) {
        return null;
    }

    @Override
    public Optional<Game> findById(String gameId) {
        return Optional.empty();
    }

    @Override
    public Game save(Game game) {
        return null;
    }
}
