package service;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static final UserService SERVICE = new UserService();
    @BeforeEach
    void deleteAll() {
        SERVICE.clear();
    }

    @Test
    void registerPositive() {
        var newUser = new RegisterRequest("username", "password", "email");
        var result = SERVICE.register(newUser);

        assertEquals("username", result.username());
        assertNotNull(result.authToken());
    }
    @Test
    void registerNegative() {
        assertThrows(Exception.class, () -> SERVICE.register(new RegisterRequest("", "", "")));
    }

    @Test
    void loginPositive() {
        SERVICE.register(new RegisterRequest("username", "password", "email"));
        var result = SERVICE.login(new LoginRequest("username", "password"));

        assertNotNull(result.authToken());
        assertEquals("username", result.username());

    }
    @Test
    void loginNegativeWrongPassword() {

        SERVICE.register(new RegisterRequest("username", "password", "email"));
        assertThrows(Exception.class, () -> SERVICE.login(new LoginRequest("username", "wrongPassword")));
    }

    @Test
    void logoutPositive() {
        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));
        String token = result.authToken();

        assertDoesNotThrow(() -> SERVICE.logout(token));
        assertThrows(Exception.class, () -> SERVICE.listGames(token));

    }

    @Test
    void logoutNegativeBadToken() {
        assertThrows(Exception.class, () -> SERVICE.logout("fake-token"));
    }

    @Test
    void listGamesPositive() {

        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));

        Collection<GamesSummary> games = SERVICE.listGames(result.authToken());
        assertEquals(0, games.size());
    }

    @Test
    void listGamesNegativeBadToken() {
        assertThrows(Exception.class, () -> SERVICE.listGames("fake-token"));
    }

    @Test

    void createGamePositive() {

        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));
        CreateGameResult createResult = SERVICE.createGame(result.authToken(), new CreateGameRequest("first game"));

        assertNotNull(createResult);
        assertNotEquals(0, createResult.gameID());

        Collection<GamesSummary> games = SERVICE.listGames(result.authToken());
        assertEquals(1, games.size());

    }

    @Test
    void createGameNegativeBadName() {

        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));

        assertThrows(Exception.class, () -> SERVICE.createGame(result.authToken(), new CreateGameRequest("")));
    }

    @Test
    void joinGamePositive() {
        var result = SERVICE.register(new RegisterRequest("owner", "password", "owner@email"));
        CreateGameResult createResult = SERVICE.createGame(result.authToken(), new CreateGameRequest("magnus was here"));

        var player = SERVICE.register(new RegisterRequest("player", "password", "player@email"));

        assertDoesNotThrow(() -> SERVICE.joinGame(result.authToken(), new JoinGameRequest("WHITE", createResult.gameID())));

        Collection<GamesSummary> games = SERVICE.listGames(result.authToken());
        assertEquals(1, games.size());

    }
    @Test
    void joinGameNegativeInvalidColor() {

        var owner = SERVICE.register(new RegisterRequest("owner", "password", "owner@email"));
        CreateGameResult createResult = SERVICE.createGame(owner.authToken(), new CreateGameRequest("game"));
        var player = SERVICE.register(new RegisterRequest("player", "password", "player@email"));

        assertThrows(Exception.class, () -> SERVICE.joinGame(player.authToken(), new JoinGameRequest("PURPLE", createResult.gameID())));

    }

    @Test
    void clearPositive() {
        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));
        SERVICE.createGame(result.authToken(), new CreateGameRequest("magnus was here"));
        SERVICE.clear();

        var newUser = SERVICE.register(new RegisterRequest("newuser", "password", "new@email"));
        Collection<GamesSummary> games = SERVICE.listGames(newUser.authToken());
        assertEquals(0, games.size());

    }

    @Test
    void clearNegativeOnEmptyService() {
        assertDoesNotThrow(() -> SERVICE.clear());
    }
}