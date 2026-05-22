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
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                SERVICE.register(new RegisterRequest("", "", ""))
        );
        assertEquals("bad request", ex.getMessage());
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
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                SERVICE.login(new LoginRequest("username", "wrongPassword"))
        );
        assertEquals("unauthorized", ex.getMessage());
    }

    @Test
    void logoutPositive() throws DataAccessException {
        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));
        String token = result.authToken();

        assertDoesNotThrow(() -> SERVICE.logout(token));
        assertThrows(DataAccessException.class, () -> SERVICE.listGames(token));
    }

    @Test
    void logoutNegativeBadToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                SERVICE.logout("fake-token")
        );
        assertEquals("unauthorized", ex.getMessage());
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));
        Collection<GamesSummary> games = SERVICE.listGames(result.authToken());
        assertEquals(0, games.size());
    }

    @Test
    void createGamePositive() throws DataAccessException {
        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));
        CreateGameResult createResult = SERVICE.createGame(result.authToken(), new CreateGameRequest("first game"));

        assertNotNull(createResult);
        assertNotEquals(0, createResult.gameID());
    }

    @Test
    void createGameNegativeBadName() throws DataAccessException {
        var result = SERVICE.register(new RegisterRequest("username", "password", "email"));
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                SERVICE.createGame(result.authToken(), new CreateGameRequest(""))
        );
        assertEquals("bad request", ex.getMessage());
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
}
