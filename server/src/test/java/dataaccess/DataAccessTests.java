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
}
