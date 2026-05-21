package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLGameDAO {

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createGame(int gameID, GameData gameInfo){
        try(var conex = DatabaseManager.getConnection()){
            var serializer = new Gson();
            var json = serializer.toJson(gameInfo);
            try(var statement = conex.prepareStatement("INSERT INTO game (gameID, json) VALUES(?,?)")){
                statement.setInt(1, gameID);
                statement.setString(2, json);
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public GameData getGame(int gameID){
        var serializer = new Gson();
        String query = "SELECT gameID, json FROM game WHERE gameID = ?";
        try(var conx = DatabaseManager.getConnection();
            var statement = conx.prepareStatement(query)){
            statement.setInt(1, gameID);
            try (var results = statement.executeQuery()){
                if (results.next()) {
                    var gameInfo = results.getString("json");
                    return serializer.fromJson(gameInfo, GameData.class);
                }
            }
            return null;
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateGame(GameData gameInfo){
        var serializer = new Gson();
        var gameID = gameInfo.gameID();
        var json = serializer.toJson(gameInfo);
        String query = "UPDATE game SET json = ? WHERE gameID = ?";
        try(var cox = DatabaseManager.getConnection();
            var statemnet = cox.prepareStatement(query)){
            statemnet.setString(1, json);
            statemnet.setInt(2, gameID);
            var results = statemnet.executeQuery();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void clear() throws DataAccessException {
        try (var cox = DatabaseManager.getConnection();
             var statement = cox.prepareStatement("TRUNCATE TABLE auth")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth table: " + e.getMessage());
        }
    }

    private final String[] createStatements = {
       """            
       CREATE TABLE if NOT EXISTS game (
                    gameID INT NOT NULL,
                    json TEXT NOT NULL,
                    PRIMARY KEY (gameID)
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
