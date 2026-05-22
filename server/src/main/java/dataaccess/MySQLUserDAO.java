package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.SQLException;

public class MySQLUserDAO {

    public MySQLUserDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            System.err.println("Unable to configure user table: " + e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        String query = "SELECT username, password, email FROM user WHERE username = ?";
        try (Connection conx = DatabaseManager.getConnection();
             var statement = conx.prepareStatement(query)) {

            statement.setString(1, username);
            try (var results = statement.executeQuery()) {
                if (results.next()) {
                    return new UserData(
                            results.getString("username"),
                            results.getString("password"),
                            results.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user: " + e.getMessage());
        }
        return null;
    }

    public void createUser(UserData user) throws DataAccessException {
        String query = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (Connection conx = DatabaseManager.getConnection();
             var statement = conx.prepareStatement(query)) {

            statement.setString(1, user.username());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.email());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating user: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        String query = "TRUNCATE TABLE user";
        try (Connection conx = DatabaseManager.getConnection();
             var statement = conx.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing user table: " + e.getMessage());
        }
    }

    private final String[] createStatements = {
            """            
        CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
        )
        """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
