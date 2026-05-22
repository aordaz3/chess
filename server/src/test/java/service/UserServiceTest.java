package service;

import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static final UserService SERVICE = new UserService();

    @BeforeEach
    void deleteAll() throws DataAccessException {
        SERVICE.clear();
    }

    @Test
    void registerPositive() throws DataAccessException {
        var newUser = new RegisterRequest("username", "password", "email");
        var result = SERVICE.register(newUser);

        assertEquals("username", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerNegative() {
        // Enforces that an empty request triggers a 400 Bad Request error
        assertThrows(BadRequestException.class, () -> SERVICE.register(new RegisterRequest("", "", "")));
    }

    @Test
    void loginPositive() throws DataAccessException {
        SERVICE.register(new RegisterRequest("username", "password", "email"));
        var result = SERVICE.login(new LoginRequest("username", "password"));

        assertNotNull(result.authToken());
        assertEquals("username", result.username());
    }

    @Test
    void loginNegativeWrongPassword() throws DataAccessException {
        SERVICE.register(new RegisterRequest("username", "password", "email"));
        // Enforces that a bad password triggers a 401 Unauthorized error
        assertThrows(UnauthorizedException.class, () -> SERVICE.login(new LoginRequest("username", "wrongPassword")));
    }

    @Test
    void logoutPositive() throws DataAccessException {
        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));
        String token = result.authToken();

        assertDoesNotThrow(() -> SERVICE.logout(token));
        // Fetching games with a dropped token should trigger a 401 Unauthorized error
        assertThrows(UnauthorizedException.class, () -> SERVICE.listGames(token));
    }

    @Test
    void logoutNegativeBadToken() {
        // Logging out with a junk token should trigger a 401 Unauthorized error
        assertThrows(UnauthorizedException.class, () -> SERVICE.logout("fake-token"));
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));

        Collection<GamesSummary> games = SERVICE.listGames(result.authToken());
        assertEquals(0, games.size());
    }

    @Test
    void listGamesNegativeBadToken() {
        // Enforces that an unauthenticated call triggers a 401 Unauthorized error
        assertThrows(UnauthorizedException.class, () -> SERVICE.listGames("fake-token"));
    }

    @Test
    void createGamePositive() throws DataAccessException {
        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));
        CreateGameResult createResult = SERVICE.createGame(result.authToken(), new CreateGameRequest("first game"));

        assertNotNull(createResult);
        assertNotEquals(0, createResult.gameID());

        Collection<GamesSummary> games = SERVICE.listGames(result.authToken());
        assertEquals(1, games.size());
    }

    @Test
    void createGameNegativeBadName() throws DataAccessException {
        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));

        // Creating a game without a name triggers a 400 Bad Request error
        assertThrows(BadRequestException.class, () -> SERVICE.createGame(result.authToken(), new CreateGameRequest("")));
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        var result = SERVICE.register(new RegisterRequest("owner", "password", "owner@email"));
        CreateGameResult createResult = SERVICE.createGame(result.authToken(), new CreateGameRequest("magnus was here"));

        SERVICE.register(new RegisterRequest("player", "password", "player@email"));

        assertDoesNotThrow(() -> SERVICE.joinGame(result.authToken(), new JoinGameRequest("WHITE", createResult.gameID())));

        Collection<GamesSummary> games = SERVICE.listGames(result.authToken());
        assertEquals(1, games.size());
    }

    @Test
    void joinGameNegativeInvalidColor() throws DataAccessException {
        var owner = SERVICE.register(new RegisterRequest("owner", "password", "owner@email"));
        CreateGameResult createResult = SERVICE.createGame(owner.authToken(), new CreateGameRequest("game"));
        var player = SERVICE.register(new RegisterRequest("player", "password", "player@email"));

        // Sending an invalid color parameter triggers a 400 Bad Request error
        assertThrows(BadRequestException.class, () -> SERVICE.joinGame(player.authToken(), new JoinGameRequest("PURPLE", createResult.gameID())));
    }

    @Test
    void clearPositive() throws DataAccessException {
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
