package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.SQLException;

public class MySQLAuthDAO {

    public MySQLAuthDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            System.err.println("Unable to configure auth table: " + e.getMessage());
        }
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        String query = "INSERT INTO auth (username, authToken) VALUES(?, ?)";
        try (Connection cox = DatabaseManager.getConnection();
             var statement = cox.prepareStatement(query)) {

            statement.setString(1, auth.username());
            statement.setString(2, auth.authToken());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth token: " + e.getMessage());
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        String query = "SELECT username, authToken FROM auth WHERE authToken = ?";
        try (Connection cox = DatabaseManager.getConnection();
             var statement = cox.prepareStatement(query)) {

            statement.setString(1, authToken);
            try (var results = statement.executeQuery()) {
                if (results.next()) {
                    return new AuthData(results.getString("authToken"), results.getString("username"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token: " + e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String query = "DELETE FROM auth WHERE authToken = ?";
        try (Connection cox = DatabaseManager.getConnection();
             var statement = cox.prepareStatement(query)) {

            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        try (Connection cox = DatabaseManager.getConnection();
             var statement = cox.prepareStatement("TRUNCATE TABLE auth")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth table: " + e.getMessage());
        }
    }

    private final String[] createStatements = {
        """            
        CREATE TABLE IF NOT EXISTS auth (
            username VARCHAR(255) NOT NULL,
            authToken VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
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
