package com.games.games_project.stubs;

import com.games.games_project.model.Game;

import java.util.Optional;

public interface UsersClient {
    Game getGameById(String gameId);

    Optional<Game> findById(String gameId);

    Game save(Game game);
}
