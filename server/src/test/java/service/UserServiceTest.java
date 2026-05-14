package service;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static final UserService service = new UserService();
    @BeforeEach
    void deleteAll() {
        service.clear();
    }

    @Test
    void register() {
        var newUser = new RegisterRequest("username", "password", "email");
        var result = service.register(newUser);

        Collection<GamesSummary> games = service.listGames(result.authToken());
        assertEquals(1, games.size());
    }

    @Test
    void login() {
        service.register(new RegisterRequest("username", "password", "email"));
        var result = service.login(new LoginRequest("username", "password"));

        assertNotNull(result.authToken());
        assertEquals("username", result.username());

    }

    @Test
    void logout() {
        var result = service.register(new RegisterRequest("username", "password", "email"));
        String token = result.authToken();

        assertDoesNotThrow(() -> service.logout(token));
        assertThrows(Exception.class, () -> service.listGames(token));

    }

    @Test
    void listGames() {

        var result = service.register(new RegisterRequest("username", "password", "email"));

        Collection<GamesSummary> games = service.listGames(result.authToken());
        assertEquals(0, games.size());
    }

    @Test

    void createGame() {

        var result = service.register(new RegisterRequest("username", "password", "email"));
        CreateGameResult createResult = service.createGame(result.authToken(), new CreateGameRequest("first game"));

        assertNotNull(createResult);
        assertNotEquals(0, createResult.gameID());

        Collection<GamesSummary> games = service.listGames(result.authToken());
        assertEquals(1, games.size());

    }

    @Test

    void joinGame() {
        var result = service.register(new RegisterRequest("owner", "password", "owner@email"));
        CreateGameResult createResult = service.createGame(result.authToken(), new CreateGameRequest("magnus was here"));

        var player = service.register(new RegisterRequest("player", "password", "player@email"));

        assertDoesNotThrow(() -> {
            service.joinGame(result.authToken(), new JoinGameRequest("WHITE", createResult.gameID()));
        });

        Collection<GamesSummary> games = service.listGames(result.authToken());
        assertEquals(1, games.size());

    }

    @Test

    void clear() {
        var result = service.register(new RegisterRequest("username", "password", "email"));
        service.createGame(result.authToken(), new CreateGameRequest("magnus was here"));
        service.clear();

        var newUser = service.register(new RegisterRequest("newuser", "password", "new@email"));
        Collection<GamesSummary> games = service.listGames(newUser.authToken());
        assertEquals(0, games.size());

    }
}