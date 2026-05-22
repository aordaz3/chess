package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessTests {

    private static MySQLUserDAO userDAO;
    private static MySQLAuthDAO authDAO;
    private static MySQLGameDAO gameDAO;

    @BeforeAll
    public static void init() {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
        gameDAO = new MySQLGameDAO();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    public void createUserPositive() throws DataAccessException {
        UserData user = new UserData("aordaz3", "password", "email");
        assertDoesNotThrow(() -> userDAO.createUser(user));

        UserData retrieved = userDAO.getUser("aordaz3");
        assertNotNull(retrieved);
        assertEquals("aordaz3", retrieved.username());
    }

    @Test
    @Order(2)
    @DisplayName("Create User - Negative (Duplicate Primary Key)")
    public void createUserNegativeDuplicate() throws DataAccessException {
        UserData user = new UserData("tungtungtungsahur", "brainrot", "funny");
        userDAO.createUser(user);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    @Order(3)
    @DisplayName("Get User - Positive")
    public void getUserPositive() throws DataAccessException {
        UserData user = new UserData("alejandro", "pi", "ordaz@chess.com");
        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser("alejandro");
        assertNotNull(retrieved);
        assertEquals("ordaz@chess.com", retrieved.email());
    }

    @Test
    public void getUserNegativeMissing() throws DataAccessException {
        UserData retrieved = userDAO.getUser("GhostPlayer");
        assertNull(retrieved);
    }

    @Test
    public void createAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token", "chess.com");
        assertDoesNotThrow(() -> authDAO.createAuth(auth));

        AuthData retrieved = authDAO.getAuth("token");
        assertNotNull(retrieved);
        assertEquals("chess.com", retrieved.username());
    }

    @Test
    public void createAuthNegativeDuplicate() throws DataAccessException {
        AuthData auth = new AuthData("same-token", "UserA");
        authDAO.createAuth(auth);

        AuthData duplicateAuth = new AuthData("same-token", "UserB");

        assertThrows(DataAccessException.class, () -> authDAO.createAuth(duplicateAuth));
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token", "karinaishot");
        authDAO.createAuth(auth);

        AuthData retrieved = authDAO.getAuth("token");
        assertNotNull(retrieved);
        assertEquals("karinaishot", retrieved.username());
    }

    @Test
    public void getAuthNegativeMissing() throws DataAccessException {
        AuthData retrieved = authDAO.getAuth("fake-token");
        assertNull(retrieved);
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("kill", "test");
        authDAO.createAuth(auth);

        assertDoesNotThrow(() -> authDAO.deleteAuth("kill"));
        assertNull(authDAO.getAuth("kill"));
    }

    @Test
    public void deleteAuthNegativeMissing() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("non-existent-token"));
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        ChessGame game = new ChessGame();
        game.getBoard().resetBoard();

        GameData gameData = new GameData(1234, null, null, "magnus", game);
        assertDoesNotThrow(() -> gameDAO.createGame(1234, gameData));

        GameData retrieved = gameDAO.getGame(1234);
        assertNotNull(retrieved);
        assertEquals("magnus", retrieved.gameName());

        assertNotNull(retrieved.game().getBoard());
        assertNotNull(retrieved.game().getBoard().getPiece(new ChessPosition(1, 1)));
    }

    @Test
    public void createGameNegativeDuplicate() throws DataAccessException {
        GameData game1 = new GameData(42, null, null, "Game 1", new ChessGame());
        gameDAO.createGame(42, game1);

        GameData game2 = new GameData(42, null, null, "Game 2", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(42, game2));
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        GameData gameData = new GameData(777, "white", "black", "test", new ChessGame());
        gameDAO.createGame(777, gameData);

        GameData retrieved = gameDAO.getGame(777);
        assertNotNull(retrieved);
        assertEquals("white", retrieved.whiteUsername());
        assertEquals("black", retrieved.blackUsername());
    }

    @Test
    public void getGameNegativeMissing() throws DataAccessException {
        GameData retrieved = gameDAO.getGame(999888);
        assertNull(retrieved);
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        ChessGame game = new ChessGame();
        game.getBoard().resetBoard();

        GameData originalGame = new GameData(555, null, null, "test", game);
        gameDAO.createGame(555, originalGame);
        ChessMove move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null); // e2 to e4
        try {
            game.makeMove(move);
        } catch (Exception e) {
            fail("Valid chess move initialization configurations failed");
        }

        GameData updatedGame = new GameData(555, "white", "black", "test", game);
        assertDoesNotThrow(() -> gameDAO.updateGame(updatedGame));

        GameData savedState = gameDAO.getGame(555);
        assertNotNull(savedState);
        assertEquals("white", savedState.whiteUsername());
        assertEquals("black", savedState.blackUsername());

        assertNull(savedState.game().getBoard().getPiece(new ChessPosition(2, 5)));
        assertNotNull(savedState.game().getBoard().getPiece(new ChessPosition(4, 5)));
    }

    @Test
    public void updateGameNegativeMissing() throws DataAccessException {
        GameData nonExistentGame = new GameData(881188, "UserA", "UserB", "Ghost Game", new ChessGame());

        assertDoesNotThrow(() -> gameDAO.updateGame(nonExistentGame));
        assertNull(gameDAO.getGame(881188));
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        gameDAO.createGame(11, new GameData(11, null, null, "G1", new ChessGame()));
        gameDAO.createGame(22, new GameData(22, null, null, "G2", new ChessGame()));

        Collection<GameData> games = gameDAO.listGames();
        assertNotNull(games);
    }
}
