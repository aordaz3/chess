package dataaccess;

import com.google.gson.Gson;
import model.*;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLAuthDAO {

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createAuth(AuthData auth) {
        try(var cox = DatabaseManager.getConnection()){
            try (var statement = cox.prepareStatement("INSERT INTO auth (username, authToken) VALUES(?, ?)")) {
                statement.setString(1, auth.username());
                statement.setString(2, auth.authToken());
                statement.executeUpdate();
            }
        }catch (SQLException | DataAccessException e){

        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try(var cox = DatabaseManager.getConnection()){
            try(var statment = cox.prepareStatement("SELECT username, authToken FROM auth WHERE authToken = ?")){
                statment.setString(1, authToken);
                try(var results = statment.executeQuery()){
                    results.next();
                    var username = results.getString("username");
                    return new AuthData(authToken, username);
                }
            }
        }catch (SQLException e){
            throw new DataAccessException("Auth Token does not exist" + authToken);
        }
    }

    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    public void clear() {
        auths.clear();
    }

    private final String[] createStatements = {
            """            
                CREATE TABLE if NOT EXISTS auth (
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
            throw new DataAccessException( String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}